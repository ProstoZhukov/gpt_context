package ru.tensor.sbis.message_panel.helper

import androidx.annotation.CheckResult
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.functions.Function4
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData
import ru.tensor.sbis.message_panel.viewModel.stateMachine.*

/**
 * @author vv.chekurda
 * @since 7/22/2019
 */


/**
 * Набор подписок на пользовательский ввод для перехода из состояния [CleanSendState] в [SimpleSendStateEvent].
 * Переход в [SimpleSendState], где мы перестаем реагировать на получателей, которые не были выбраны пользователем,
 * происходит если пользователь: начинает вводить сообщение, прикрепил вложение, выбрал получателя вручную,
 * цитирует сообщение.
 */
@CheckResult
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
internal fun observeUserChanges(liveData: MessagePanelLiveData, machine: MessagePanelStateMachine<*, *, *>): Disposable {
    val handler = { _: Any? -> machine.fire(EventUserInput()) }
    return CompositeDisposable(
            liveData.messageText.filter { !it.value.isNullOrBlank() }.subscribe(handler),
            liveData.attachments.filter { it.isNotEmpty() }.subscribe(handler),
            liveData.recipientsSelected.filter { it }.subscribe(handler),
            liveData.quotePanelVisible.filter { it }.subscribe(handler)
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
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
internal fun observeUserChangesClear(liveData: MessagePanelLiveData, machine: MessagePanelStateMachine<*, *, *>): Disposable {
    val handler = { _: Any? -> machine.fire(EventUserInputClear()) }
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
                    .subscribe(handler)
    )
}

/**
 * Подписка на управление видимостью панели аудио записи
 */
@CheckResult
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
internal fun observeRecorderViewVisibility(
    liveData: MessagePanelLiveData,
    recordObservers: Observable<Boolean>
): Observable<Boolean> {
    return Observable.combineLatest(
        liveData.isEditing,
        liveData.messageText.map { it.value.isNullOrEmpty() },
        liveData.attachments.map { it.isEmpty() },
        recordObservers
    ) { isEditing, textIsEmpty, attachmentsIsEmpty, hasRecordObservers ->
        !isEditing && textIsEmpty && attachmentsIsEmpty && hasRecordObservers
    }
}