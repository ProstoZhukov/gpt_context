package ru.tensor.sbis.message_panel.core.state_machine

import ru.tensor.sbis.common.rx.scheduler.TensorSchedulers
import ru.tensor.sbis.common.util.statemachine.StateMachineImpl
import ru.tensor.sbis.common.util.statemachine.StateMachineInner
import ru.tensor.sbis.message_panel.core.state_machine.config.StateMachineConfig
import ru.tensor.sbis.message_panel.declaration.vm.MessagePanelViewModel

/**
 * TODO: 11/11/2020 Добавить документацию https://online.sbis.ru/opendoc.html?guid=27078b6d-5ded-4c38-a504-ef29e4c6c902
 *
 * @author ma.kolpakov
 */
internal class DefaultMessagePanelStateMachine(
    config: StateMachineConfig<MessagePanelStateMachine, MessagePanelViewModel>,
    viewModel: MessagePanelViewModel
) :
    MessagePanelStateMachine,
    StateMachineInner by StateMachineImpl(TensorSchedulers.androidUiScheduler) {

    init {
        config.transitions.forEach { (eventType, transition) ->
            state(eventType) { transition.invoke(this, viewModel, it) }
        }

        setState(config.initialState)
    }
}