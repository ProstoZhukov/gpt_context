package ru.tensor.sbis.message_panel.viewModel.stateMachine

import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.util.statemachine.SessionStateEvent
import ru.tensor.sbis.message_panel.helper.observeUserChanges
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel

/**
 * Состояние перед отправкой сообщения, при котором пользователь ещё не произвёл каких либо действий с панелью ввода
 *
 * @author vv.chekurda
 * @since 7/18/2019
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
open class CleanSendState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(
    viewModel: MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>,
    private val needToClean: Boolean
) : BaseSendState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(viewModel) {

    protected open val targetStateEvent: SessionStateEvent = SimpleSendStateEvent()

    init {
        addOnSetAction { cleanAction(liveData, viewModel, needToClean) }
        addOnSetAction { disposer += observeUserChanges(liveData, viewModel.stateMachine) }

        event(EventRecipients::class) { viewModel.loadRecipients(it.recipients, it.isUserSelected, it.add) }
        event(EventUserInput::class) { fire(targetStateEvent) }
        //ивент срабатывает, когда приходит актуальная ConversationInfo
        event(EventEnable::class) { viewModel.resetConversationInfo() }
    }
}