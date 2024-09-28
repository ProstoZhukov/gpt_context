package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.delegates.scroll

import io.reactivex.disposables.Disposable
import ru.tensor.sbis.base_components.adapter.selectable.SelectionHelper
import ru.tensor.sbis.communicator.common.conversation.ConversationEventsPublisher
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.persons.ConversationRegistryItem
import java.util.*

/**
 * Реализация делегата [ScrollToConversationDelegate]
 *
 * @property conversationEventsPublisher шина событий отправки новых сообщений пользователем
 * @property selectionHelper             вспомогательный класс для работы с выделенными элементами списка на планшете
 *
 * @author vv.chekurda
 */
internal class ScrollToConversationDelegateImpl private constructor(
    private val conversationEventsPublisher: ConversationEventsPublisher,
    private val selectionHelper: SelectionHelper<ConversationRegistryItem>,
    private val actionsHolder: ActionsHolder
) : ScrollToConversationDelegate,
    ScrollToConversationActions by actionsHolder {

    constructor(
        conversationEventsPublisher: ConversationEventsPublisher,
        selectionHelper: SelectionHelper<ConversationRegistryItem>
    ) : this(
        conversationEventsPublisher,
        selectionHelper,
        ActionsHolder()
    )

    /**
     * Идентификатор переписки, в которой было отправлено сообщение
     */
    private var targetConversationUuid: UUID? = null

    /**
     * Подписка на события отправки сообщений пользователем для планшета
     */
    override fun subscribeOnScrollToConversation(
        actions: ScrollToConversationActions
    ): Disposable =
        conversationEventsPublisher
            .messageSentObservable
            .filter { selectionHelper.isTablet }
            .subscribe(::onMessageSent)
            .also { actionsHolder.init(actions) }

    /**
     * Проверка необходимости скролла к переписке
     */
    override fun onDataListUpdated() {
        // Выделенные элементы есть только на планшете
        if (!selectionHelper.isTablet) return

        val selectedConversation = selectionHelper.selectedItem.castTo<ConversationModel>()
        val targetUuid = targetConversationUuid
        when {
            // При активном поиске в реестре не пытаемся скролиться
            getSearchQuery().isNotBlank() -> return
            // Идентификатор переписки, в которой было отправлено сообщение, совпадает с выделенным -> скролимся к ней
            targetUuid != null && selectedConversation?.uuid == targetUuid -> scrollToConversation(targetUuid)
            // Если идентификаторы не совпадают -> выделение элемента сброшено или сообщение исходило из другой переписки
            else                                                           -> targetConversationUuid = null
        }
    }

    /**
     * Обработка события о новом исходящем сообщении
     *
     * @param conversationUuid идентификатор переписки, в которой было новое исходящее сообщение
     */
    private fun onMessageSent(conversationUuid: UUID) {
        targetConversationUuid = conversationUuid
    }

    /**
     * Осуществить скролл к переписке в реестре
     *
     * @param uuid идентификатор переписки, к которой необходимо подскроллиться
     */
    private fun scrollToConversation(uuid: UUID) {
        val position = getDataList().indexOfFirst { it.castTo<ConversationModel>()?.uuid == uuid }
        when {
            // Элемент в зоне досягаемости списка, happy path сценарий -> скроллимся к нему
            position != -1       -> {
                scrollToPosition(position)
                targetConversationUuid = null
            }
            // Элемента нет в списке, и мы не паджинировались -> куда-то исчез(удалили, перенесли, баг контроллера), ничего не далаем
            getListOffset() == 0 -> {
                targetConversationUuid = null
            }
            // Элемента нет в новом списке, мы спаджинировались -> идём искать в начало
            else                 -> {
                scrollToTop()
            }
        }
    }
}

/**
 * Холдер методов для отложенного делегирования реализаций управляющего интерфейса
 * @see ScrollToConversationActions
 */
private class ActionsHolder : ScrollToConversationActions {
    override lateinit var getDataList: () -> List<ConversationRegistryItem>
    override lateinit var getListOffset: () -> Int
    override lateinit var getSearchQuery: () -> String
    override lateinit var scrollToPosition: (Int) -> Unit
    override lateinit var scrollToTop: () -> Unit

    fun init(actions: ScrollToConversationActions) {
        getDataList = actions.getDataList
        getListOffset = actions.getListOffset
        getSearchQuery = actions.getSearchQuery
        scrollToPosition = actions.scrollToPosition
        scrollToTop = actions.scrollToTop
    }
}