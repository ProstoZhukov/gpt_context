package ru.tensor.sbis.message_panel.core.state_machine.config

import ru.tensor.sbis.common.util.statemachine.SessionState
import ru.tensor.sbis.common.util.statemachine.SessionStateEvent
import ru.tensor.sbis.message_panel.core.state_machine.MessagePanelStateMachine
import ru.tensor.sbis.message_panel.core.state_machine.event.transition.*
import ru.tensor.sbis.message_panel.core.state_machine.state.*
import ru.tensor.sbis.message_panel.core.state_machine.state.config.SimpleSendingStateConfig
import ru.tensor.sbis.message_panel.declaration.vm.MessagePanelViewModel
import kotlin.reflect.KClass

/**
 * TODO: 11/11/2020 Добавить документацию https://online.sbis.ru/opendoc.html?guid=27078b6d-5ded-4c38-a504-ef29e4c6c902
 */
typealias Transition<MACHINE, VM, EVENT> = MACHINE.(viewModel: VM, event: EVENT) -> Unit

/**
 * TODO: 11/11/2020 Добавить документацию https://online.sbis.ru/opendoc.html?guid=27078b6d-5ded-4c38-a504-ef29e4c6c902
 *
 * @author ma.kolpakov
 */
class StateMachineConfig<MACHINE : MessagePanelStateMachine, VM : MessagePanelViewModel>
private constructor(
    val initialState: SessionState
) {
    private val transitionsMap: MutableMap<KClass<out SessionStateEvent>, Transition<MACHINE, VM, SessionStateEvent>> =
        HashMap()

    val transitions: Map<KClass<out SessionStateEvent>, Transition<MACHINE, VM, SessionStateEvent>> = transitionsMap

    fun <EVENT : SessionStateEvent> addTransition(
        eventType: KClass<out EVENT>,
        transition: Transition<MACHINE, VM, EVENT>
    ) {
        @Suppress("UNCHECKED_CAST")
        transitionsMap[eventType] = transition as Transition<MACHINE, VM, SessionStateEvent>
    }

    companion object {

        fun <MACHINE : MessagePanelStateMachine, VM : MessagePanelViewModel> createEmpty(
            initialState: SessionState
        ) = StateMachineConfig<MACHINE, VM>(initialState)

        fun <MACHINE : MessagePanelStateMachine, VM : MessagePanelViewModel> createDefault(
            initialState: SessionState = DisabledState()
        ) = StateMachineConfig<MACHINE, VM>(initialState).apply {
            addTransition(DisabledStateEvent::class) { _, _ -> setState(DisabledState()) }

            addTransition(DraftLoadingStateEvent::class) { vm, event ->
                setState(DraftLoadingState(vm, event.documentUuid, event.conversationUuid, event.needToClean))
            }

            addTransition(CleanStateEvent::class) { vm, event -> setState(CleanSendState(vm, event.needToClean)) }
            addTransition(SimpleSendStateEvent::class) { vm, _ -> setState(SimpleSendState(vm)) }

            addTransition(EditingStateEvent::class) { vm, event -> setState(EditingState(vm, event.eventEdit)) }
            addTransition(QuotingStateEvent::class) { vm, event -> setState(QuotingState(vm, event.eventQuote)) }
            addTransition(SharingStateEvent::class) { vm, event -> setState(SimpleSendState(vm, event.content)) }
            addTransition(ReplayingStateEvent::class) { vm, event -> setState(ReplayingState(vm, event.eventReplay)) }
            addTransition(SendingEditMessageEvent::class) { vm, event ->
                setState(SendingEditMessageState(vm, event.messageUuid))
            }
            addTransition(SendingAudioMessageEvent::class) { vm, event ->
                setState(SendingAudioMessageState(vm, event.attachment))
            }
            addTransition(SendingQuoteMessageEvent::class) { vm, event ->
                setState(SendingMessageState(vm, SimpleSendingStateConfig(event.quotedMessageUuid)))
            }
            addTransition(SendingSimpleMessageEvent::class) { vm, _ ->
                setState(SendingMessageState(vm))
            }
        }
    }
}