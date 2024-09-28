package ru.tensor.sbis.recorder.decl

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.common_attachments.Attachment

/**
 * Интерфейс сервиса аудио записи для работы в RecorderView
 *
 * @author vv.chekurda
 * @since 7/25/2019
 */
interface RecorderService : Disposable {

    /**
     * Подписка на получение записи для отправки
     */
    val recordFile: Observable<Attachment>

    /**
     * Запуск записи аудио
     */
    fun startRecord()

    /**
     * Остановка записи аудио
     */
    fun stopRecord()

    /**
     * Прерывание записи аудио
     */
    fun cancelRecord()
}
