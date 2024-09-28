package ru.tensor.sbis.design.message_panel.audio_recorder.integration

import androidx.fragment.app.Fragment
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.design.message_panel.audio_recorder.MessagePanelAudioRecorderPlugin.callStateProvider
import ru.tensor.sbis.design.message_panel.audio_recorder.integration.contract.AudioRecorderDelegate
import ru.tensor.sbis.design.message_panel.audio_recorder.integration.contract.AudioRecorderDelegateFactory
import ru.tensor.sbis.design.message_panel.audio_recorder.view.AudioRecordView
import ru.tensor.sbis.design.message_panel.audio_recorder.view.contract.AudioRecordMode
import ru.tensor.sbis.design.message_panel.audio_recorder.view.contract.AudioRecordResultData
import ru.tensor.sbis.design.message_panel.audio_recorder.view.contract.AudioRecordViewState
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.emotion_picker.MessageEmotionPicker
import ru.tensor.sbis.design.message_panel.decl.record.RecordControlButtonPosition
import ru.tensor.sbis.message_panel.contract.MessagePanelController
import ru.tensor.sbis.message_panel.helper.media.AudioRecordData
import ru.tensor.sbis.message_panel.view.MessagePanel
import ru.tensor.sbis.design.message_panel.recorder_common.integration.BaseRecorderDelegate
import ru.tensor.sbis.message_panel.helper.media.MediaRecordData

/**
 * Реализация делегата компонента записи аудиосообщений [AudioRecorderDelegate].
 *
 * @author vv.chekurda
 */
internal class AudioRecorderDelegateImpl private constructor(
    fragment: Fragment,
    messagePanel: MessagePanel,
    messagePanelController: MessagePanelController<*, *, *>,
    private val audioRecordView: AudioRecordView,
    private val emotionPicker: MessageEmotionPicker? = null
) : BaseRecorderDelegate<AudioRecordViewState, AudioRecordResultData>(
    fragment = fragment,
    messagePanel = messagePanel,
    messagePanelController = messagePanelController,
    recorder = audioRecordView,
    callStateProvider = callStateProvider
), AudioRecorderDelegate {

    companion object : AudioRecorderDelegateFactory {
        override fun createRecorderDelegate(
            fragment: Fragment,
            messagePanel: MessagePanel,
            messagePanelController: MessagePanelController<*, *, *>,
            audioRecordView: AudioRecordView,
            emotionPicker: MessageEmotionPicker?,
        ): AudioRecorderDelegate =
            AudioRecorderDelegateImpl(
                fragment = fragment,
                messagePanel = messagePanel,
                messagePanelController = messagePanelController,
                audioRecordView = audioRecordView,
                emotionPicker = emotionPicker
            )
    }

    private var requireEmotionPicker = false

    init {
        audioRecordView.mode = AudioRecordMode.MESSAGE_PANEL
        emotionPicker?.also(messagePanel::addKeyboardEventDelegate)
        initMessageSendingListener()
    }

    override fun hideEmotionPicker() {
        emotionPicker?.hide()
    }

    override fun release() {
        super.release()
        emotionPicker?.clear()
        emotionPicker?.onKeyboardCloseMeasure(0)
        hideEmotionPicker()
    }

    override fun onPause() {
        super.onPause()
        hideEmotionPicker()
    }

    override fun onBeforeStartRecording() {
        super.onBeforeStartRecording()
        hideEmotionPicker()
        audioRecordView.pressedButtonPanelPosition =
            if (messagePanel.isVideoRecordEnabled) RecordControlButtonPosition.SECOND_ALIGN_END
            else RecordControlButtonPosition.FIRST_ALIGN_END
    }

    override fun onRecordCompleted(resultData: AudioRecordResultData) {
        if (resultData.emotion == null) {
            requireEmotionPicker = true
            showEmotionPicker()
        }
        super.onRecordCompleted(resultData)
    }

    override fun getMediaRecordData(resultData: AudioRecordResultData): MediaRecordData =
        with(resultData) {
            AudioRecordData(
                audioFile,
                duration,
                waveform,
                emotion?.code
            )
        }

    private fun initMessageSendingListener() {
        messagePanelController.viewModel
            .messageSending
            .subscribe {
                if (requireEmotionPicker) {
                    requireEmotionPicker = false
                } else {
                    hideEmotionPicker()
                }
            }.storeIn(disposer)
    }

    /**
     * Показать панель выбора эмоции для аудиосообщения.
     */
    private fun showEmotionPicker() {
        emotionPicker?.show { emotion ->
            emotion?.code?.also(messagePanelController::editMediaMessageEmotion)
        }
    }
}