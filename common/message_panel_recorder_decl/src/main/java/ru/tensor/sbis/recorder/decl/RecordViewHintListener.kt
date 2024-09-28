package ru.tensor.sbis.recorder.decl

/**
 * Обработчик подписки на отображение подсказки об аудиозаписи
 *
 * @author vv.chekurda
 * Создан 7/31/2019
 */
interface RecordViewHintListener {

    /**
     * Индикатор необходимости показа уведомления
     */
    fun onShowHint(show: Boolean)
}
