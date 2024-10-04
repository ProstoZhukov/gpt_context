package ru.tensor.sbis.design.message_panel.audio_recorder.view.contract

import ru.tensor.sbis.design.message_panel.audio_recorder.view.AudioRecordView
import ru.tensor.sbis.design.message_panel.decl.record.RecordControlButtonPosition
import ru.tensor.sbis.design.message_panel.recorder_common.contact.BaseRecordViewApi

/**
 * API компонента записи аудиосообщения.
 * @see AudioRecordView
 *
 * @author vv.chekurda
 */
interface AudioRecordViewApi : BaseRecordViewApi<AudioRecordViewState, AudioRecordResultData> {

    /**
     * Режим работы записи аудиосообщения.
     * По умолчанию [AudioRecordMode.SIMPLE]
     */
    var mode: AudioRecordMode

    /**
     * Исходная позиция зажатой кнопки записи в панели.
     * Использовать в ситуациях,
     * когда пользователь может зажать кнопку записи и в панели всего одна запись - аудио, видео нет.
     */
    var pressedButtonPanelPosition: RecordControlButtonPosition
}