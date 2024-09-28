package ru.tensor.sbis.recorder.decl

/**
 * Подписка на события RecorderView
 *
 * @author vv.chekurda
 * Создан 8/9/2019
 */
interface RecorderViewListener {

    /**
     * Началась звукозапись
     */
    fun onRecordStarted()

    /**
     * Звукозапись завершена или отменена
     */
    fun onRecordCompleted()
}
