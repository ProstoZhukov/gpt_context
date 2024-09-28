package ru.tensor.sbis.message_panel.viewModel.stateMachine

import ru.tensor.sbis.common.util.statemachine.SessionState
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel

/**
 * @author Subbotenko Dmitry
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
class DisabledState(viewModel: MessagePanelViewModel<*, *, *>) : SessionState() {
    init {
        viewModel.liveData.applyDisabledHint()

        event(EventDisable::class) {
            // актуализация подсказки в панели ввода
            viewModel.liveData.applyDisabledHint()
        }
        event(EventEnable::class) { fire(CleanStateEvent()) }
        event(EventEnableWithDraft::class) {
            fire(
                DraftLoadingStateEvent(
                    it.documentUuid,
                    it.conversationUuid,
                    it.isNewConversation,
                    it.recipients,
                    needToClean = true
                )
            )
        }
    }
}