package ru.tensor.sbis.base_components.adapter.selectable

import androidx.annotation.VisibleForTesting
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Класс для работы с выделением строк на планшете и обработки тапа по элементам списка.
 * В зависимости от конфигурации, если планшет, то выполняет выделение элемента через адаптер, запоминает выделенный
 * элемент и испускает событие нажатия элемента. Если конфигурации - не планшет, не производит выделение элемента и
 * испускает событие нажатия элемента с тротлингом, чтобы предотвратить открытие нескольких детальных экранов при
 * быстром повтроном тапе.
 */
class SelectionHelper<ITEM_TYPE : Any> @VisibleForTesting constructor(
    var isTablet: Boolean,
    private var itemComparator: ItemComparator<ITEM_TYPE>,
    private var itemSelectionSubject: PublishSubject<ITEM_TYPE>,
    private val throttleUntilChangedPredicate: ThrottleUntilChangedPredicate<ITEM_TYPE> = ThrottleUntilChangedPredicate()
) {
    private var _selectedItem: ITEM_TYPE? = null
    private var adapter: SelectableListAdapter<ITEM_TYPE>? = null

    /**
     * Получить текущий выбранный элемент.
     */
    val selectedItem: ITEM_TYPE get() = _selectedItem ?: adapter!!.provideStubItem()

    /**
     * Наблюдатель события смены выбранного элемента. Событие происходит только при нажатие на отличный от
     * предыдущего элемент.
     */
    val itemSelectionObservable: Observable<ITEM_TYPE> = itemSelectionSubject.filter(throttleUntilChangedPredicate)!!

    @JvmOverloads
    constructor(
        isTablet: Boolean,
        itemComparator: ItemComparator<ITEM_TYPE> = ItemComparator<ITEM_TYPE> { item1: ITEM_TYPE, item2: ITEM_TYPE ->
            item1 == item2
        }
    ) : this(isTablet, itemComparator, PublishSubject.create())

    init {
        /**
         * Этот вызов можно убрать, когда isTablet будет удален из конструктора в пользу вызова не Deprecated
         * метода attachAdapter.
         */
        setShouldThrottle()
    }

    /**
     * Прикрепить адаптер [adapter], будет удерживать ссылку на него до вызова метода detachAdapter.
     */
    @Deprecated("Использовать метод с параметром isTablet", ReplaceWith("attachAdapter(adapter, isTablet)"))
    fun attachAdapter(adapter: SelectableListAdapter<ITEM_TYPE>) {
        if (this.adapter == adapter) return
        this.adapter = adapter
        adapter.attachSelectionHelper(this)
        if (isTablet) {
            this.adapter!!.onItemSelected(adapter.provideStubItem(), selectedItem)
        }
    }

    /**
     * Прикрепить адаптер [adapter] и указать признак планшетной конфигурации, будет удерживать ссылку на адаптер до
     * вызова метода detachAdapter.
     */
    fun attachAdapter(adapter: SelectableListAdapter<ITEM_TYPE>, isTablet: Boolean) {
        this.isTablet = isTablet
        setShouldThrottle()
        if (this.adapter == adapter) return
        this.adapter = adapter
        adapter.attachSelectionHelper(this)
        if (isTablet) {
            this.adapter!!.onItemSelected(adapter.provideStubItem(), selectedItem)
        }
    }

    /**
     * Освободить ссылку на адаптер, прикрепленный через метод [attachAdapter].
     */
    fun detachAdapter() {
        if (adapter == null) return
        adapter!!.detachSelectionHelper(this)
        adapter = null
    }

    /**
     * Указать [item] как выделенный элемент, событие смены выделенного элемента можно отследить
     * через [itemSelectionObservable].
     */
    fun selectItem(item: ITEM_TYPE) {
        val selectionChanged = changeSelection(item)
        if (!isTablet) {
            // for non-tablet devices always emit selection to open details screen for selected item
            emit()
        } else if (selectionChanged) {
            emit()
        }
    }

    /**
     * Сменить выделенный элемент на [item], событие смены выделенного элемента не произойдет, метод вернет true,
     * если [item] отличается от указанного выделенным элемента прежде.
     */
    fun changeSelection(item: ITEM_TYPE): Boolean {
        val selectionChanged = adapter == null ||
                !itemComparator.equals(selectedItem, item)
        if (adapter != null && isTablet && selectionChanged) {
            adapter!!.onItemSelected(item, selectedItem)
        }
        _selectedItem = item
        return selectionChanged
    }

    /**
     * Сбросить выделенние элемента на тот, что возвращает [adapter.provideStubItem()]
     */
    fun resetSelection() {
        if (adapter == null) return

        throttleUntilChangedPredicate.ignoreSelectionThrottling = true
        selectItem(adapter!!.provideStubItem())
        throttleUntilChangedPredicate.ignoreSelectionThrottling = false
    }

    private fun setShouldThrottle() {
        throttleUntilChangedPredicate.shouldThrottle = !isTablet
    }

    private fun emit() {
        itemSelectionSubject.onNext(selectedItem)
    }
}