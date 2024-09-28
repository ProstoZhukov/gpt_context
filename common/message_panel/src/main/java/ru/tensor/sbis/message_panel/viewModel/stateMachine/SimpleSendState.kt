package ru.tensor.sbis.message_panel.viewModel.stateMachine

import io.reactivex.Single
import io.reactivex.functions.BiFunction
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.message_panel.helper.observeUserChangesClear
import ru.tensor.sbis.message_panel.model.ShareContent
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel

/**
 * @author Subbotenko Dmitry
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
open class SimpleSendState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(
    viewModel: MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>,
    sharedContent: ShareContent? = null
) : BaseSendState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(viewModel) {

    init {
        addOnSetAction { sharedContent?.let { viewModel.setSharedContent(it) } }
        addOnSetAction { disposer += observeUserChangesClear(liveData, viewModel.stateMachine) }
        event(EventRecipients::class) { handleRecipientsEvent(it) }
        event(EventUserInputClear::class) { fire(CleanStateEvent(false)) }
        event(EventSign::class) { fire(SendingSignMessageEvent(it.action)) }
        event(EventEnable::class) {
            viewModel.resetRecipients()
            viewModel.resetConversationInfo()
        }
    }

    /**
     * Подставляем получателей, если:
     * - они были выбраны пользователем [EventRecipients.isUserSelected]
     * - панель находится в режиме ммода нового сообщения. Допущение о том, что в этом режиме получатели не
     * устанавливаются в режиме "живой переписки"
     */
    private fun handleRecipientsEvent(event: EventRecipients) {
        disposer += Single.zip(
            Single.just(event.isUserSelected),
            liveData.newDialogModeEnabled.firstOrError(),
            BiFunction<Boolean, Boolean, Boolean> { byUser, newMessageMode ->
                byUser || newMessageMode
            })
            .filter { it }
            .subscribe {
                viewModel.loadRecipients(event.recipients, event.isUserSelected, event.add)
            }
    }
}