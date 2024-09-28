package ru.tensor.sbis.design.message_panel.audio_recorder.integration.contract

import androidx.fragment.app.Fragment
import ru.tensor.sbis.design.message_panel.audio_recorder.view.AudioRecordView
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.emotion_picker.MessageEmotionPicker
import ru.tensor.sbis.message_panel.contract.MessagePanelController
import ru.tensor.sbis.message_panel.view.MessagePanel
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика для создания делегата записи аудиосообщений [AudioRecorderDelegate].
 *
 * @author vv.chekurda
 */
interface AudioRecorderDelegateFactory : Feature {

    /**
     * Создать делегата записи аудиосообщений [AudioRecorderDelegate].
     */
    fun createRecorderDelegate(
        fragment: Fragment,
        messagePanel: MessagePanel,
        messagePanelController: MessagePanelController<*, *, *>,
        audioRecordView: AudioRecordView,
        emotionPicker: MessageEmotionPicker? = null
    ): AudioRecorderDelegate
}