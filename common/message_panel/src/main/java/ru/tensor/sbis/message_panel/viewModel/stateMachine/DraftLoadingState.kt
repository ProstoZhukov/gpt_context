package ru.tensor.sbis.message_panel.viewModel.stateMachine

import io.reactivex.Single
import io.reactivex.disposables.Disposable
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.util.statemachine.SessionEvent
import ru.tensor.sbis.common.util.statemachine.SessionStateEvent
import ru.tensor.sbis.communicator.generated.MessageTextWithMentions
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import timber.log.Timber
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
open class DraftLoadingState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(
    viewModel: MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>,
    private val documentUuid: UUID?,
    private val conversationUuid: UUID?,
    val isNewConversation: Boolean = false,
    val recipients: List<UUID> = emptyList(),
    val needToClean: Boolean = true,
    val clearDraft: Boolean = false
) : BaseState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(viewModel) {

    protected open val targetStateEvent: SessionStateEvent = SimpleSendStateEvent()
    private var loadDraftDisposable: Disposable? = null

    /**
     * Отложенные состояния, которые не могут быть обработаны на этапе загрузки черновика.
     * События дублируются в целевое [targetStateEvent] или [ignoreDraftStateEvent].
     */
    private val pendingEvents: MutableList<SessionEvent> = CopyOnWriteArrayList()

    /**
     * Событие по умолчанию для перехода в случае отсутствия черновика или, если его нужно игнорировать
     */
    private val defaultDraftIgnoreState = CleanStateEvent(false)

    /**
     * Целевое событие для случаев, когда черновик нужно игнорировать (он будет перезаписан сразу после установки)
     */
    @Volatile
    private var ignoreDraftStateEvent: SessionStateEvent? = null

    /**
     * Источник, при подписке на который можно получить черновик. Способ чтения черновика может меняться
     * если панель ввода показана для создания нового диалога
     */
    private val draftSource: Single<out DRAFT_RESULT>
        get() = draftInteractor.loadDraft(conversationUuid, documentUuid, clearDraft)

    init {
        addOnSetAction {
            cleanAction(
                liveData = liveData,
                viewModel = viewModel,
                cleanText = needToClean,
                // Для избежания смаргивания - не чистим получателей до получения модели драфта.
                cleanRecipients = false
            )
        }
        addOnSetAction { loadDraft() }
        event(EventDisable::class) { fire(DisabledStateEvent()) }
        event(EventQuote::class) { fire(QuotingStateEvent(it)) }
        event(EventReplay::class) { ignoreDraftStateEvent = ReplayingStateEvent(it) }
        event(EventRecipients::class) { pendingEvents.add(it) }
        //событие срабатывает, когда приходит актуальная ConversationInfo
        event(EventEnable::class) { viewModel.resetConversationInfo() }
        event(EventShare::class) {
            pendingEvents.add(it)
            // в ответ можно публиковать события, затирать его не нужно
            if (ignoreDraftStateEvent !is ReplayingStateEvent) {
                /*
                Получили событие шаринга, нет смысла устанавливать данные черновика.
                Сразу направляемся в нужное событие
                 */
                ignoreDraftStateEvent = defaultDraftIgnoreState
            }
        }
    }

    private fun loadDraft() {
        liveData.resetDraftUuid()
        loadDraftDisposable = draftSource.subscribe(
            { content ->
                liveData.setDraftUuid(draftResultHelper.getId(content))
                val recipientUuidList = draftResultHelper.getRecipients(content)
                viewModel.attachmentPresenter.loadAttachmentsFromDraft()
                if (ignoreDraftStateEvent == null && !draftResultHelper.isEmpty(content)) {
                    val messageText = draftResultHelper.getText(content)
                    liveData.setMessageText(messageText)
                    liveData.setAnsweredMessageUuid(draftResultHelper.getAnsweredMessageId(content))
                    // получатели из черновика -> выбраны пользователем -> переопределить нельзя
                    viewModel.loadRecipients(recipientUuidList, true)
                    // проверяем наличие цитирования в черновике
                    val quote = draftResultHelper.getQuoteContent(content)
                    if (quote != null) {
                        // переводим в состояние цитирования
                        fire(EventQuote(quote, false))
                    } else {
                        // переводим в целевое состояние в последнюю очередь, чтобы не реагировать на изменения контента
                        fire(targetStateEvent)
                    }
                    val mentionsJson = draftResultHelper.getServiceObject(content) ?: StringUtils.EMPTY
                    liveData.setDraftMentions(MessageTextWithMentions(messageText, mentionsJson))
                } else {
                    // чистка текста необходима для возможности плавного перехода без смаргиваний к драфтовому тексту
                    liveData.setMessageText("")
                    // устанавливаем получателей, но разрешаем переопределить т.к. другого контента нет
                    if (recipientUuidList.isNotEmpty()) {
                        viewModel.loadRecipients(recipientUuidList, false)
                    } else if (viewModel.shouldClearRecipients()) {
                        viewModel.clearRecipients()
                    }
                    // очищать контент не нужно. На этапе загрузки черновика с панелью ввода ещё не работали
                    fire(ignoreDraftStateEvent ?: defaultDraftIgnoreState)
                    // черновика нет или игнорируется. Продублируем пользовательские события в целевое состояние
                    pendingEvents.onEach { viewModel.stateMachine.fire(it) }
                }
            },
            {
                Timber.e(it, "Error loading draft")
                fire(CleanStateEvent())
            }
        )
    }
}