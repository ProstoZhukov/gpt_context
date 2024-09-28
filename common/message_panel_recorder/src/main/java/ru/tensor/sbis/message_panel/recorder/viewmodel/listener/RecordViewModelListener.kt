package ru.tensor.sbis.message_panel.recorder.viewmodel.listener

import ru.tensor.sbis.message_panel.recorder.viewmodel.RecorderIconState
import ru.tensor.sbis.message_panel.recorder.viewmodel.RecorderViewModel
import ru.tensor.sbis.recorder.decl.RecordViewHintListener

/**
 * Подписка на события [RecorderViewModel]
 *
 * @author vv.chekurda
 * Создан 7/27/2019
 */
internal interface RecordViewModelListener : RecordViewHintListener {

    /**
     * Состояние/внешний вид иконки микрофона
     */
    fun onStateChanged(state: RecorderIconState)

    /**
     * Счётчик записанного времени
     */
    fun onTimeChanged(time: String)
}