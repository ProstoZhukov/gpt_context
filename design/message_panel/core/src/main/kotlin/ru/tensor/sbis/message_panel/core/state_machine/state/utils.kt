/**
 * Набор инструментов для отслеживание состояний
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.message_panel.core.state_machine.state

import androidx.annotation.CheckResult
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function4
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.message_panel.core.state_machine.event.transition.DraftLoadingStateEvent
import ru.tensor.sbis.message_panel.declaration.vm.MessagePanelViewModel


/**
 * Набор подписок на пользовательский ввод для перехода из состояния [CleanSendState] в [SimpleSendStateEvent].
 * Переход в [SimpleSendState], где мы перестаем реагировать на получателей, которые не были выбраны пользователем,
 * происходит если пользователь: начинает вводить сообщение, прикрепил вложение, выбрал получателя вручную,
 * цитирует сообщение.
 */
@CheckResult
internal fun observeUserChanges(liveData: MessagePanelLiveData, consumer: Consumer<Any>): Disposable {
    return CompositeDisposable(
            liveData.messageText.filter { !it.value.isNullOrBlank() }.subscribe(consumer),
            liveData.attachments.filter { it.isNotEmpty() }.subscribe(consumer),
            liveData.recipientsSelected.filter { it }.subscribe(consumer),
            liveData.quotePanelVisible.filter { it }.subscribe(consumer)
    )
}

/**
 * Набор подписок на пользовательский ввод для обратного перехода в чистое состояние из [SimpleSendStateEvent] в [CleanSendState].
 * Переход в [CleanSendState] будет осуществлён при четырёх одновременных условиях: пользователь удалил текст набранного сообщения,
 * нет прикреплённых вложений, нет цитируемого сообщения и если ранее не был выбран получатель вручную.
 * Если получатель был выбран вручную, то переход к [CleanSendState] произойдет только после отправки сообщения или
 * при перезаходе в переписку.
 */
@CheckResult
internal fun observeUserChangesClear(liveData: MessagePanelLiveData, fireInputEvent: Consumer<Any>): Disposable {
    val observeMessageText = liveData.messageText
    val observeAttachments = liveData.attachments
    val observeIsRecipientsSelected = liveData.recipientsSelected
    val observeQuotePanelVisibility = liveData.quotePanelVisible
    return CompositeDisposable(
            Observable.combineLatest(observeMessageText, observeAttachments, observeIsRecipientsSelected, observeQuotePanelVisibility,
                    Function4 { T1: RxContainer<String>, T2: List<*>, T3: Boolean, T4: Boolean ->
                        T1.value.isNullOrBlank() && T2.isEmpty() && !T3 && !T4
                    })
                    .filter { it }
                    .subscribe(fireInputEvent)
    )
}

/**
 * Подписка на управление видимостью панели аудио записи
 */
@CheckResult
internal fun observeRecorderViewVisibility(liveData: MessagePanelLiveData): Observable<Boolean> {
    return Observable.combineLatest(
        liveData.messageText.map { it.value.isNullOrEmpty() },
        liveData.attachments.map { it.isEmpty() },
        BiFunction { textIsEmpty, attachmentsIsEmpty -> textIsEmpty && attachmentsIsEmpty }
    )
}

/**
 * Общая механика инициализации для [CleanSendState] и [DraftLoadingState]
 */
internal fun cleanAction(
    liveData: MessagePanelLiveData,
    viewModel: MessagePanelViewModel,
    needToClean: Boolean
) {
    liveData.setRecipientsSelected(false)

    if (needToClean) {
        viewModel.clearRecipients()
        liveData.setMessageText("")
    }
    viewModel.resetConversationInfo()
}

/**
 * Перевод машины в состояние презагрузки черновика
 */
internal fun AbstractMessagePanelState<*>.loadDraftForNewMessage(
    coreConversationInfo: CoreConversationInfo,
    needToClean: Boolean
) {
    coreConversationInfo.run {
        fire(
            DraftLoadingStateEvent(
                document,
                conversationUuid,
                needToClean
            )
        )
    }
}