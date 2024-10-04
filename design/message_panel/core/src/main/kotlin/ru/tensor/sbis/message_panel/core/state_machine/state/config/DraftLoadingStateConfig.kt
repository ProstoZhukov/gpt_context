package ru.tensor.sbis.message_panel.core.state_machine.state.config

import io.reactivex.Single
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.common.util.statemachine.SessionEvent
import ru.tensor.sbis.common.util.statemachine.SessionStateEvent
import ru.tensor.sbis.message_panel.core.state_machine.config.StateConfig
import ru.tensor.sbis.message_panel.core.state_machine.event.action.EventEnable
import ru.tensor.sbis.message_panel.core.state_machine.event.action.EventRecipients
import ru.tensor.sbis.message_panel.core.state_machine.event.action.EventReplay
import ru.tensor.sbis.message_panel.core.state_machine.event.action.EventShare
import ru.tensor.sbis.message_panel.core.state_machine.event.transition.CleanStateEvent
import ru.tensor.sbis.message_panel.core.state_machine.event.transition.ReplayingStateEvent
import ru.tensor.sbis.message_panel.core.state_machine.event.transition.SharingStateEvent
import ru.tensor.sbis.message_panel.core.state_machine.event.transition.SimpleSendStateEvent
import ru.tensor.sbis.message_panel.core.state_machine.state.AbstractMessagePanelState
import ru.tensor.sbis.message_panel.declaration.vm.MessagePanelViewModel
import timber.log.Timber
import java.util.*

/**
 * TODO: 11/12/2020 Добавить документацию https://online.sbis.ru/opendoc.html?guid=27078b6d-5ded-4c38-a504-ef29e4c6c902
 *
 * @author ma.kolpakov
 */
class DraftLoadingStateConfig(
    private val documentUuid: UUID?,
    private val conversationUuid: UUID?,
    private val needToClean: Boolean,
    private val targetStateEvent: SessionStateEvent = SimpleSendStateEvent
) : StateConfig<MessagePanelViewModel, AbstractMessagePanelState<MessagePanelViewModel>> {

    private var loadDraftDisposable: Disposable? = null
    /**
     * Черновики, в общем случае, загружаются быстро и пользовательские события прилетают в нужное состояние. НО, порой,
     * случаются задержки и события требуется перенаправлять т.к. состояние загрузки черновика с ними работать не умеет
     */
    @Volatile
    private var pendingEvent: SessionEvent? = null

    /**
     * Источник, при подписке на который можно получить черновик. Способ чтения черновика может меняться
     * если панель ввода показана для создания нового диалога
     */
    private val draftSource: Single<out DRAFT_RESULT>
        get() = draftInteractor.loadDraft(conversationUuid, documentUuid)

    override fun apply(state: AbstractMessagePanelState<MessagePanelViewModel>): Disposable {
        state.addOnSetAction { cleanAction(liveData, viewModel, needToClean) }
        state.addOnSetAction { loadDraft() }

        state.event(EventReplay::class) {
            loadDraftDisposable?.dispose()
            state.fire(ReplayingStateEvent(it))
        }
        state.event(EventRecipients::class) { pendingEvent = it }
        //событие срабатывает, когда приходит актуальная ConversationInfo
        state.event(EventEnable::class) { viewModel.resetConversationInfo() }
        state.event(EventShare::class) {
            loadDraftDisposable?.dispose()
            state.fire(SharingStateEvent(it.content))
        }
    }

    private fun loadDraft() {
        liveData.resetDraftUuid()
        loadDraftDisposable = draftSource.subscribe(
            { content ->
                liveData.setDraftUuid(draftResultHelper.getId(content))
                val recipientUuidList = draftResultHelper.getRecipients(content)
                if (!draftResultHelper.isEmpty(content)) {
                    liveData.setMessageText(draftResultHelper.getText(content))
                    // получатели из черновика -> выбраны пользователем -> переопределить нельзя
                    viewModel.loadRecipients(recipientUuidList, true)
                    // переводим в целевое состояние в последнюю очередь, чтобы не реагировать на изменения контента
                    fire(targetStateEvent)
                } else {
                    // устанавливаем получателей, но разрешаем переопределить т.к. другого контента нет
                    if (recipientUuidList.isNotEmpty()) {
                        viewModel.loadRecipients(recipientUuidList, false)
                    }
                    // очищать контент не нужно. На этапе загрузки черновика с панелью ввода ещё не работали
                    fire(CleanStateEvent(false))
                    // черновика нет. Продублируем пользовательские события в целевое состояние
                    pendingEvent?.let { viewModel.stateMachine.fire(it) }
                }
            },
            {
                Timber.e(it, "Error loading draft")
                fire(CleanStateEvent())
            }
        )
    }
}