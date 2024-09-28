package ru.tensor.sbis.message_panel.core.state_machine.state.config

import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import ru.tensor.sbis.message_panel.core.state_machine.config.StateConfig
import ru.tensor.sbis.message_panel.core.state_machine.event.action.*
import ru.tensor.sbis.message_panel.core.state_machine.event.transition.*
import ru.tensor.sbis.message_panel.core.state_machine.state.AbstractMessagePanelState
import ru.tensor.sbis.message_panel.core.state_machine.state.CleanSendState
import ru.tensor.sbis.message_panel.core.state_machine.state.SimpleSendState
import ru.tensor.sbis.message_panel.declaration.vm.MessagePanelViewModel

/**
 * Конфигурация базового состояния перед отправкой сообщения, здесь находятся общие реакции на события для:
 * - [CleanSendState] - чистого состояния (до ввода пользователя в панель)
 * - [SimpleSendState] - состояния, в которое входим после любых ввыодов в панель от пользователя
 *
 * @author ma.kolpakov
 */
object SendStateConfig : StateConfig<MessagePanelViewModel, AbstractMessagePanelState<*>> {

    override fun apply(state: AbstractMessagePanelState<*>): Disposable {
        state.event(EventSend::class) { state.fire(SendingSimpleMessageEvent) }
        state.event(EventReplay::class) { state.fire(ReplayingStateEvent(it)) }
        state.event(EventDisable::class) { state.fire(DisabledStateEvent) }
        state.event(EventEdit::class) { state.fire(EditingStateEvent(it)) }
        state.event(EventQuote::class) { state.fire(QuotingStateEvent(it)) }
        /*
        Отправка должна осуществляться только с "чистого состояния".
        Можем позволить подписку в базовом классе так как функционал отключается скрытием элемента на уровне UI.
        Правила скрытия описаны в observeRecorderViewVisibility()
         */
        state.event(EventAudio::class) { state.fire(SendingAudioMessageEvent(it.attachment)) }
        state.event(EventShare::class) { state.fire(SharingStateEvent(it.content)) }

        return Disposables.disposed()
    }
}