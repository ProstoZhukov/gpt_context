package ru.tensor.sbis.message_panel.viewModel.livedata.recipients

import io.reactivex.subjects.PublishSubject

/**
 * Реализация [CustomRecipientSelectionMediator], отправляет события:
 * запрос показа экрана выбора получателя сообщения
 * запрос очистки списка исполнителей
 * Удалить после https://online.sbis.ru/doc/83240201-d85d-4fa1-aa2f-3c1f1287904f
 *
 * @author ra.stepanov
 */
internal class CustomRecipientSelectionMediatorImpl: CustomRecipientSelectionMediator {

    /**
     * Событие сигнализирующее о том, что следует открыть прикладное окно выбора получателей
     */
    override val requestCustomRecipient = PublishSubject.create<Unit>()

    /**
     * Событие сигнализирующее о том, что следует очистить список выбранных получателей на стороне прикладного кода
     */
    override val clearCustomRecipients = PublishSubject.create<Unit>()

    /**
     * Запросить прикладной код открыть экран выбора получателей
     */
    override fun requestCustomRecipients() = requestCustomRecipient.onNext(Unit)

    /**
     * Запросить прикладной код очистить выбранных получателей
     */
    override fun clearCustomRecipients() = clearCustomRecipients.onNext(Unit)
}