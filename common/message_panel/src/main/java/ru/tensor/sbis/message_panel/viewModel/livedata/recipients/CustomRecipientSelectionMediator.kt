package ru.tensor.sbis.message_panel.viewModel.livedata.recipients

import io.reactivex.Observable

/**
 * Интерфейс медиатора для работы с выбором получателей сообщения
 * Удалить после https://online.sbis.ru/doc/83240201-d85d-4fa1-aa2f-3c1f1287904f
 *
 * @author vv.chekurda
 */
interface CustomRecipientSelectionMediator {

    /**
     * Событие сигнализирующее о том, что следует открыть прикладное окно выбора получателей
     */
    val requestCustomRecipient: Observable<Unit>

    /**
     * Событие сигнализирующее о том, что следует очистить список выбранных получателей на стороне прикладного кода
     */
    val clearCustomRecipients: Observable<Unit>

    /**
     * Запросить прикладной код открыть экран выбора получателей
     */
    fun requestCustomRecipients()

    /**
     * Запросить прикладной код очистить выбранных получателей
     */
    fun clearCustomRecipients()
}