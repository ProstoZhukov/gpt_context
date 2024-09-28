package ru.tensor.sbis.message_panel.viewModel.stateMachine

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.common.rx.livedata.value
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.util.statemachine.SessionStateEvent
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.OpenedByRequest
import timber.log.Timber

/**
 * @author Subbotenko Dmitry
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
internal class ReplayingState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(
    viewModel: MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>,
    eventReplay: EventReplay
) : BaseState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(viewModel) {

    private var startSpawn: String? = null
    private val sharedTextSubject = BehaviorSubject.createDefault("")

    init {
        addOnSetAction { replyComment(eventReplay) }

        event(EventSend::class) { fire(SendingSimpleMessageEvent()) }
        event(EventReplay::class) { changeState(ReplayingStateEvent(it)) }
        event(EventDisable::class) { changeState(DisabledStateEvent()) }
        event(EventEdit::class) { changeState(EditingStateEvent(it)) }
        event(EventCancel::class) { changeState(CleanStateEvent(false)) }
        event(EventShare::class) { (content) ->
            // не затираем пользовательский ввод, если делятся только вложениями
            content.text.takeIf { it.isNotBlank() }?.let { sharedTextSubject.onNext(it) }
            viewModel.attachmentPresenter.addAttachments(content.fileUriList)
        }
    }

    private fun changeState(state: SessionStateEvent) {
        startSpawn?.let {
            val value = liveData.messageText.value
            liveData.setMessageText(value?.removePrefix(it))
            startSpawn = null
        }

        fire(state)
    }

    private fun replyComment(eventReplay: EventReplay) {
        val loadMessageMaybe = messageInteractor.getMessageByUuid(
            eventReplay.messageUuid,
            eventReplay.conversationUuid,
            eventReplay.documentUuid
        ).flatMapMaybe { message ->
            if (messageResultHelper.isResultError(message)) {
                Timber.e("Conversation data failure %s", messageResultHelper.getResultError(message))
                Maybe.empty()
            } else
                Maybe.just(message)
        }.cache()

        disposer += loadMessageMaybe.subscribe { message ->
            if (eventReplay.showKeyboard) {
                liveData.postKeyboardEvent(OpenedByRequest)
            }

            liveData.setConversationUuid(eventReplay.conversationUuid)
            liveData.setDocumentUuid(eventReplay.documentUuid)
            liveData.setAnsweredMessageUuid(eventReplay.messageUuid)
        }

        val senderMaybe = loadMessageMaybe.map { messageResultHelper.getSender(it) }
        disposer += senderMaybe.subscribe { sender ->
            // комментарий, а соответственно получателя, выбирает пользователь - обеспечиваем подстановку получателя
            sender.uuid.let { viewModel.loadRecipients(listOf(it), isUserSelected = true) }
        }

        // компоновка имени пользователя со строкой, которой поделились
        val senderNameMaybe = senderMaybe.map { it.name.firstName }
        disposer += Observable.combineLatest(
            senderNameMaybe.toObservable(),
            sharedTextSubject,
            { sender, text -> sender to text }
        ).subscribe { (sender, text) ->
            startSpawn = "${sender}, $text"
            liveData.setMessageText(startSpawn)
        }
    }
}