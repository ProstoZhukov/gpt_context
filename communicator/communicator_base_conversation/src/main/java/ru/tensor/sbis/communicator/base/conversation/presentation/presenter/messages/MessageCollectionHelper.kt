package ru.tensor.sbis.communicator.base.conversation.presentation.presenter.messages

import android.util.Log
import ru.tensor.sbis.communicator.base.conversation.presentation.crud.ConversationListComponent
import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage
import ru.tensor.sbis.communicator.base.conversation.presentation.crud.ConversationListSizeSettings
import ru.tensor.sbis.communicator.core.data.events.MessagesEvent
import ru.tensor.sbis.crud4.view.datachange.ItemInserted
import ru.tensor.sbis.crud4.view.datachange.ItemRemoved
import ru.tensor.sbis.service.generated.DirectionStatus
import java.util.UUID

/**
 * Вспомогательная реализация реестра сообщений по работе с навигацией списка.
 *
 * @author vv.chekurda
 */
internal class MessageCollectionHelper<MESSAGE : BaseConversationMessage>(
    private val collectionComponent: ConversationListComponent<MESSAGE>,
    private val visibleMessagesHelper: VisibleMessagesHelper<MESSAGE>,
    private var dataList: List<MESSAGE> = emptyList(),
) {
    private var isWaitingPagination: Boolean = false
    private var isLoadNewerDisabled: Boolean = false

    /**
     * Признак того, что пользователь находится на границе списка в сторону новых сообщений.
     */
    private val isInNewerBorder: Boolean
        get() = visibleMessagesHelper.firstVisibleItem <= ConversationListSizeSettings.paginationReserveCount

    /**
     * Признак того, что пользователь находится на границе списка в сторону старых сообщений.
     */
    private val isInOlderBorder: Boolean
        get() = dataList.size - visibleMessagesHelper.lastVisibleItem <= ConversationListSizeSettings.paginationReserveCount

    /**
     * Признак нахожденя на самой нижней границе сообщений в переписке.
     */
    val atBottomOfMessages: Boolean
        get() = visibleMessagesHelper.atBottomOfList && !hasNewerPage

    /**
     * Признак валидности отображаемого списка.
     */
    var isValidListData: Boolean = true

    /**
     * Признак возможности начать новую пагинацию.
     */
    private val canTryLoadPage: Boolean
        get() = dataList.isNotEmpty()
            && isValidListData
            && !isWaitingPagination

    /**
     * Признак наличия страницы более старых сообщений.
     */
    var hasOlderPage: Boolean = false
        private set

    /**
     * Признак наличия страницы более новых сообщений.
     */
    var hasNewerPage: Boolean = false
        private set

    /**
     * Признак процесса перехода к целевому сообщению [targetMessageUuid].
     */
    var isMovingToTarget: Boolean = false
        private set

    /**
     * Ключ активного запроса синхронизации.
     */
    var activeRequestId: String? = null
        private set

    /**
     * Целевое сообщение, к которому необходимо осуществить навигационный переход.
     * null - конец переписки.
     * [isMovingToTarget] - признак процесса перехода к сообщению.
     */
    var targetMessageUuid: UUID? = null
        private set

    /**
     * Признак необходимости подсветить [targetMessageUuid] после окончания перехода.
     */
    var highlightTarget: Boolean = false
        private set

    /**
     * Признак видимости прогресса загрузки более новых сообщений.
     */
    val isNewerProgressVisible: Boolean
        get() = collectionComponent.loadPreviousThrobberIsVisible.value == true

    init {
        collectionComponent.onEndUpdate.observeForever { status ->
            onEndUpdate(status)
        }
    }

    /**
     * Установить целевое сообщение [targetMessageUuid].
     * [highlight] true, если необходимо подсветить сообщение после перехода.
     */
    fun setupTarget(messageUuid: UUID?, highlight: Boolean = false) {
        targetMessageUuid = messageUuid
        highlightTarget = highlight
        activeRequestId = TARGET_MESSAGE_REQUEST_PREFIX + messageUuid
        isMovingToTarget = true
    }

    /**
     * Очистить целевое сообщение.
     */
    fun clearTarget() {
        targetMessageUuid = null
        highlightTarget = false
        isMovingToTarget = false
        activeRequestId = null
    }

    /**
     * Закрыть активный [activeRequestId].
     */
    fun cancelActiveRequestId() {
        activeRequestId = null
    }

    /**
     * Проверить является ли текущий колбэк ответом на активный запрос [activeRequestId].
     */
    fun isActiveRequestCallback(params: HashMap<String, String>): Boolean =
        params.containsKey(MessagesEvent.REQUEST_ID.type) && params[MessagesEvent.REQUEST_ID.type] == activeRequestId

    /**
     * Список сообщений изменился.
     */
    fun onDataListChanged(dataList: List<MESSAGE>) {
        this.dataList = dataList
    }

    /**
     * Список сообщений проскролили.
     */
    fun onScroll(
        dy: Int,
        firstVisibleItemPosition: Int,
        lastVisibleItemPosition: Int
    ) {
        visibleMessagesHelper.onVisibleItemsChanged(firstVisibleItemPosition, lastVisibleItemPosition)
        tryPaginateOnScroll(dy)
    }

    /**
     * Обновить индексы видимых элементов по событию [event].
     */
    fun updateVisibleIndexes(event: ItemInserted<MESSAGE>) {
        if (event.isNewerInsertion) {
            val addCount = event.indexItemList.size
            visibleMessagesHelper.onVisibleItemsChanged(
                visibleMessagesHelper.firstVisibleItem + addCount,
                visibleMessagesHelper.lastVisibleItem + addCount
            )
        }
    }

    /**
     * Обновить индексы видимых элементов по событию [event].
     */
    fun updateVisibleIndexes(event: ItemRemoved<MESSAGE>) {
        if (event.isNewerRemoving) {
            val removeCount = event.indexes.size
            visibleMessagesHelper.onVisibleItemsChanged(
                visibleMessagesHelper.firstVisibleItem - removeCount,
                visibleMessagesHelper.lastVisibleItem - removeCount
            )
        }
    }

    /**
     * Обработать событие начала перехода к новой коллекции после нажатия кнопки в самый низ.
     */
    fun onFastScrollDown() {
        isLoadNewerDisabled = true
    }

    private fun onEndUpdate(status: DirectionStatus) {
        hasOlderPage = status.forward
        hasNewerPage = status.backward && !isLoadNewerDisabled
        isWaitingPagination = false
        Log.d("ConversationCollection", "onEndUpdate hasOlderPage $hasOlderPage, hasNewerPage $hasNewerPage")
    }

    private fun tryLoadNewer() {
        if (hasNewerPage && canTryLoadPage && isInNewerBorder) {
            isWaitingPagination = true
            collectionComponent.loadPrevious()
        }
    }

    private fun tryLoadOlder() {
        if (hasOlderPage && canTryLoadPage && isInOlderBorder) {
            isWaitingPagination = true
            isLoadNewerDisabled = false
            collectionComponent.loadNext()
        }
    }

    private fun tryPaginateOnScroll(dy: Int) {
        when {
            dy > 0 -> tryLoadNewer()
            dy < 0 -> tryLoadOlder()
        }
    }
}

internal val ItemInserted<*>.isNewerInsertion: Boolean
    get() = indexItemList.first().first == 0L

internal fun ItemInserted<*>.isInsertionInVisiblePositions(visibleMessages: VisibleMessagesHelper<*>): Boolean =
    visibleMessages.isAnyVisible(indexItemList.map { it.first })

internal val ItemInserted<*>.isOlderInsertion: Boolean
    get() = indexItemList.first().first.toInt() == allItems.lastIndex - indexItemList.lastIndex

internal val ItemRemoved<*>.isNewerRemoving: Boolean
    get() = indexes.first() == 0L

private const val TARGET_MESSAGE_REQUEST_PREFIX = "target-"