package ru.tensor.sbis.design_selection_common.controller

import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.communication_decl.selection.SelectionItemId
import ru.tensor.sbis.design_selection.contract.controller.SelectionDelegate
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.data.SelectionItemMapper
import ru.tensor.sbis.recipients.generated.CollectionObserverOfRecipientViewModel
import ru.tensor.sbis.recipients.generated.CollectionOfRecipientViewModel
import ru.tensor.sbis.recipients.generated.ItemWithIndexOfRecipientViewModel
import ru.tensor.sbis.recipients.generated.PaginationOfRecipientAnchor
import ru.tensor.sbis.recipients.generated.RecipientController
import ru.tensor.sbis.recipients.generated.RecipientControllerProvider
import ru.tensor.sbis.recipients.generated.RecipientFilter
import ru.tensor.sbis.recipients.generated.RecipientId
import ru.tensor.sbis.recipients.generated.RecipientViewModel
import ru.tensor.sbis.service.generated.IndexPair
import ru.tensor.sbis.service.generated.StubType
import ru.tensor.sbis.service.generated.ViewPosition

/**
 * Адаптер для контроллера выбора получателей.
 *
 * Покрывает следующие архитектурные изъяны контроллера:
 * 1) На каждое обновление фильтра вместо новой коллекции должен создаваться новый инстанс контроллера
 * для создания коллекции. И соответственно все операции по выбору/отмене выбора/загрузке списка выбранных
 * должны производится с нового инстанса контроллера, который привязан к текущей отображаемой коллекции.
 * @see createCollection
 *
 * 2) При любом изменении фильтра необходимо перезапрашивать список выбранных, но только после фактического
 * пересоздания нового инстанса контроллера для новой коллекции.
 * @see setOnFilterChangedCallback
 *
 * 3) При выборе со сбросом фильтра контроллер продолжает обновлять коллекцию после вызова выбора,
 * в то время как список в ui компоненте уже пустой из-за вызова reset с новым фильтром.
 * @see createCollectionObserver
 * @see select
 *
 * 4) Выбранные получатели берутся из разных зарегестрированных источников, из-за этого может возникнуть ситуация,
 * что выбранного контакта нет в кэше лиц для создания задачи. Данный изъян вынуждает импортить выбранных получателей
 * во внешние источники.
 * @see sourcesImportHelper
 * @see getAllSelectedItems
 *
 * @author vv.chekurda
 */
class SelectionControllerAdapter<ITEM : SelectionItem>(
    private val controllerProvider: Lazy<RecipientControllerProvider>,
    private val mapper: SelectionItemMapper<RecipientViewModel, RecipientId, ITEM, SelectionItemId>,
    private val sourcesImportHelper: SelectionSourcesImportHelper? = null
) : SelectionDelegate<ITEM> {

    private var controllerInstance: RecipientController? = null

    @get:Synchronized
    private val controller: RecipientController
        get() = controllerInstance
            ?: controllerProvider.value.let { provider ->
                controllerInstance = provider.createController()
                controllerInstance!!
            }

    @get:Synchronized
    private var onFilterChangedCallback: (() -> Unit)? = null
    @get:Synchronized
    private var lastFilter: RecipientFilter? = null

    @get:Synchronized
    private var collectionObserver: CollectionObserverWrapper? = null
    private var isCollectionCreated: Boolean = false

    private val SelectionItemId.nativeId: RecipientId
        get() = mapper.getId(this)

    override fun getSelectedItems(): List<ITEM> =
        controller.getSelectedRecipients(isConfirming = false).map(mapper::map)

    override fun getAllSelectedItems(): List<ITEM> {
        val selected = controller.getSelectedRecipients(isConfirming = true)
        sourcesImportHelper?.importSelectedRecipients(selected)
        return selected.map(mapper::map)
    }

    override fun select(id: SelectionItemId, withNotify: Boolean) {
        if (!withNotify) collectionObserver?.cancel()
        val status = controller.select(id.nativeId)
        if (status.errorCode != ErrorCode.SUCCESS) {
            throw RuntimeException(status.errorMessage)
        }
    }

    override fun singleComplete(id: SelectionItemId): ITEM {
        select(id, withNotify = false)
        val selectedItem = getAllSelectedItems().first()
        unselect(id)
        return selectedItem
    }

    override fun replaceSelected(id: SelectionItemId, withNotify: Boolean) {
        if (!withNotify) collectionObserver?.cancel()
        controller.replaceSelected(id.nativeId)
    }

    override fun unselect(id: SelectionItemId) {
        controller.unselect(id.nativeId)
    }

    override fun hasSelectedItems(): Boolean =
        controller.hasSelectedRecipients()

    override fun setOnFilterChangedCallback(callback: (() -> Unit)?) {
        onFilterChangedCallback = callback
    }

    /**
     * Создать коллекцию [CollectionOfRecipientViewModel].
     */
    fun createCollection(
        filter: RecipientFilter,
        anchor: PaginationOfRecipientAnchor
    ): CollectionOfRecipientViewModel {
        checkControllerInstance()
        val collection = controller.get(filter, anchor)
        checkFilterChanges(filter)
        return collection
    }

    /**
     * Создать наблюдателя [CollectionObserverOfRecipientViewModel] для коллекции.
     */
    fun createCollectionObserver(
        observer: CollectionObserverOfRecipientViewModel
    ): CollectionObserverOfRecipientViewModel =
        CollectionObserverWrapper(observer).also {
            collectionObserver = it
        }

    /**
     * Метод для проверки и обновления инстанса контроллера.
     * Ограничение контроллера - для каждого инстанса контроллера может быть только одна коллекция.
     */
    private fun checkControllerInstance() {
        if (isCollectionCreated) {
            controllerInstance = null
        } else {
            isCollectionCreated = true
        }
    }

    /**
     * Проверка изменения фильтра [filter] с отправкой колбэка.
     * В частности необходима для оповещения обновления фильтра на контроллере
     * для безопасного обновления списка уже выбранных.
     */
    private fun checkFilterChanges(filter: RecipientFilter) {
        if (filter != lastFilter) {
            lastFilter = filter
            onFilterChangedCallback?.invoke()
        }
    }

    private class CollectionObserverWrapper(
        private val observer: CollectionObserverOfRecipientViewModel
    ) : CollectionObserverOfRecipientViewModel() {

        @get:Synchronized
        private var isCancelled: Boolean = false

        fun cancel() {
            isCancelled = true
        }

        private fun safeCancelled(action: () -> Unit) {
            if (!isCancelled) action()
        }

        override fun onReset(items: ArrayList<RecipientViewModel>) {
            safeCancelled { observer.onReset(items) }
        }

        override fun onRemove(index: ArrayList<Long>) {
            safeCancelled { observer.onRemove(index) }
        }

        override fun onMove(param: ArrayList<IndexPair>) {
            safeCancelled { observer.onMove(param) }
        }

        override fun onAdd(param: ArrayList<ItemWithIndexOfRecipientViewModel>) {
            safeCancelled { observer.onAdd(param) }
        }

        override fun onReplace(param: ArrayList<ItemWithIndexOfRecipientViewModel>) {
            safeCancelled { observer.onReplace(param) }
        }

        override fun onAddThrobber(position: ViewPosition) {
            safeCancelled { observer.onAddThrobber(position) }
        }

        override fun onRemoveThrobber() {
            safeCancelled { observer.onRemoveThrobber() }
        }

        override fun onAddStub(stubType: StubType, position: ViewPosition) {
            safeCancelled { observer.onAddStub(stubType, position) }
        }

        override fun onRemoveStub() {
            safeCancelled { observer.onRemoveStub() }
        }

        override fun onBeginUpdate() {
            safeCancelled { observer.onBeginUpdate() }
        }

        override fun onEndUpdate() {
            safeCancelled { observer.onEndUpdate() }
        }
    }
}