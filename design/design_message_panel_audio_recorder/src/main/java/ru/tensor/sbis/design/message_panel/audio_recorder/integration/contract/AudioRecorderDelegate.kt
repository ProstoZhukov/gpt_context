package ru.tensor.sbis.design.message_panel.audio_recorder.integration.contract

import ru.tensor.sbis.design.message_panel.audio_recorder.view.AudioRecordView
import ru.tensor.sbis.design.message_panel.audio_recorder.view.contract.AudioRecordResultData
import ru.tensor.sbis.design.message_panel.audio_recorder.view.contract.AudioRecordViewState
import ru.tensor.sbis.design.message_panel.recorder_common.integration.RecorderDelegate
import ru.tensor.sbis.message_panel.view.MessagePanel

/**
 * Делегат компонента записи аудиосообщений [AudioRecordView] для подключения к панели сообщений [MessagePanel].
 *
 * @author vv.chekurda
 */
interface AudioRecorderDelegate : RecorderDelegate<AudioRecordViewState, AudioRecordResultData> {

    /**
     * Скрыть панель выбора эмоции для аудиосообщения.
     * Зовется автоматически в сценариях записи аудиосообщений.
     */
    fun hideEmotionPicker()
}