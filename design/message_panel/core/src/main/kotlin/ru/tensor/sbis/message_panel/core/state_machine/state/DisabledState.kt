package ru.tensor.sbis.message_panel.core.state_machine.state

import ru.tensor.sbis.common.util.statemachine.SessionState
import ru.tensor.sbis.message_panel.core.state_machine.event.action.EventEnable
import ru.tensor.sbis.message_panel.core.state_machine.event.action.EventEnableWithDraft
import ru.tensor.sbis.message_panel.core.state_machine.event.transition.CleanStateEvent
import ru.tensor.sbis.message_panel.core.state_machine.event.transition.DraftLoadingStateEvent

/**
 * @author ma.kolpakov
 */
class DisabledState : SessionState() {

    init {
        event(EventEnable::class) { fire(CleanStateEvent()) }
        event(EventEnableWithDraft::class) {
            fire(DraftLoadingStateEvent(it.documentUuid, it.conversationUuid, true))
        }
    }
}