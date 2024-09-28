package ru.tensor.sbis.message_panel.viewModel.stateMachine

import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel

/**
 * Общее базовое состояние перед отправкой сообщения, здесь находятся общие реакции на ивенты
 * для унаследованных [CleanSendState] - чистого состояния (до ввода пользователя в панель) и
 * [SimpleSendState] - состояния, в которое входим после любых ввыодов в панель от пользователя
 *
 * @author vv.chekurda
 * @since 05/08/2019
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
open class BaseSendState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(
    viewModel: MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>
) : BaseState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(viewModel) {
    init {
        addOnSetAction { liveData.applyEnabledHint() }
        event(EventSend::class) { fire(SendingSimpleMessageEvent()) }
        event(EventReplay::class) { fire(ReplayingStateEvent(it)) }
        event(EventDisable::class) { fire(DisabledStateEvent()) }
        event(EventEdit::class) { fire(EditingStateEvent(it)) }
        event(EventQuote::class) { fire(QuotingStateEvent(it)) }
        /*
        Отправка должна осуществляться только с "чистого состояния".
        Можем позволить подписку в базовом классе так как функционал отключается скрытием элемента на уровне UI.
        Правила скрытия описаны в observeRecorderViewVisibility()
         */
        event(EventSendMediaMessage::class) { fire(SendingMediaMessageEvent(it.attachment, it.metaData)) }
        event(EventShare::class) { fire(SharingStateEvent(it.content)) }
    }
}