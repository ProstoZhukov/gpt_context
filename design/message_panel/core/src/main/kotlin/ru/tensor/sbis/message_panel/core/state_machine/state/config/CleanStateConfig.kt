package ru.tensor.sbis.message_panel.core.state_machine.state.config

import io.reactivex.disposables.Disposable
import ru.tensor.sbis.common.util.statemachine.SessionStateEvent
import ru.tensor.sbis.message_panel.core.state_machine.config.StateConfig
import ru.tensor.sbis.message_panel.core.state_machine.event.action.EventEnable
import ru.tensor.sbis.message_panel.core.state_machine.event.action.EventRecipients
import ru.tensor.sbis.message_panel.core.state_machine.event.action.EventUserInput
import ru.tensor.sbis.message_panel.core.state_machine.state.AbstractMessagePanelState
import ru.tensor.sbis.message_panel.core.state_machine.state.observeUserChanges
import ru.tensor.sbis.message_panel.declaration.vm.MessagePanelViewModel

/**
 * TODO: 11/12/2020 Добавить документацию https://online.sbis.ru/opendoc.html?guid=27078b6d-5ded-4c38-a504-ef29e4c6c902
 *
 * @author ma.kolpakov
 */
class CleanStateConfig(
    private val needToClean: Boolean,
    private val targetStateEvent: SessionStateEvent
) : StateConfig<MessagePanelViewModel, AbstractMessagePanelState<MessagePanelViewModel>> {

    override fun apply(state: AbstractMessagePanelState<MessagePanelViewModel>): Disposable {
        lateinit var disposable: Disposable
        state.addOnSetAction { cleanAction(liveData, state.vm, needToClean) }
        state.addOnSetAction { disposable = observeUserChanges(liveData) { state.fire(EventUserInput) } }

        state.event(EventRecipients::class) { state.vm.loadRecipients(it.recipients, it.isUserSelected) }
        state.event(EventUserInput::class) { state.fire(targetStateEvent) }
        //ивент срабатывает, когда приходит актуальная ConversationInfo
        state.event(EventEnable::class) { state.vm.resetConversationInfo() }

        return disposable
    }
}