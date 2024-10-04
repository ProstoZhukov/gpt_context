package ru.tensor.sbis.design.message_panel.audio_recorder.recorder

import android.app.Activity
import ru.tensor.sbis.design.message_panel.audio_recorder.MessagePanelAudioRecorderPlugin.waveformHelperProvider
import ru.tensor.sbis.design.message_panel.decl.record.MediaFileFactory
import ru.tensor.sbis.design.message_panel.recorder_common.audio_record.AudioRecorder
import ru.tensor.sbis.design.message_panel.recorder_common.audio_record.AudioRecordListener
import ru.tensor.sbis.design.message_panel.recorder_common.utils.MUXER_OUTPUT_FLAC
import ru.tensor.sbis.design.message_panel.recorder_common.utils.MediaMuxerWrapper
import ru.tensor.sbis.design.message_panel.recorder_common.utils.RecordingDeviceHelper

/**
 * Менеджер для записи аудио.
 *
 * @property fileFactory фабрика для создания файлов.
 * @property permissionsHelper помощник для запроса разрешений аудиозаписи.
 * @property deviceHelper помощник для настройки девайса перед записью.
 *
 * @author vv.chekurda
 */
internal class AudioRecordManager(
    private val fileFactory: MediaFileFactory,
    private val permissionsHelper: AudioPermissionsHelper,
    private val deviceHelper: RecordingDeviceHelper
) {

    constructor(
        activity: Activity,
        fileFactory: MediaFileFactory
    ) : this(
        fileFactory = fileFactory,
        permissionsHelper = AudioPermissionsHelper(activity),
        deviceHelper = RecordingDeviceHelper(activity)
    )

    private val audioRecorder = AudioRecorder(waveformHelper = waveformHelperProvider.provideAudioWaveformHelper())

    /**
     * Слушатель процесса записи.
     */
    var processListener: AudioRecordListener?
        get() = audioRecorder.recordListener
        set(value) {
            audioRecorder.recordListener = value
        }

    /**
     * Выполнить действие при наличии разрешений.
     * При отсутствии сделает запрос.
     */
    fun withPermissions(action: () -> Unit) {
        permissionsHelper.withPermissions(action)
    }

    /**
     * Подготовить компонент к записи.
     */
    fun prepareRecorder() {
        deviceHelper.configureDevice(isStartRecording = true)
        val file = fileFactory.createFile()
        val muxer = MediaMuxerWrapper(file, outputFormat = MUXER_OUTPUT_FLAC)
        audioRecorder.prepare(muxer)
    }

    /**
     * Начать запись.
     */
    fun startRecording() {
        audioRecorder.startRecording()
    }

    /**
     * Остановить запись.
     */
    fun stopRecording(withUnlockConfig: Boolean = true) {
        if (withUnlockConfig) {
            deviceHelper.configureDevice(isStartRecording = false)
        }
        audioRecorder.stopRecording()
    }

    /**
     * Отменить запись.
     */
    fun cancelRecording() {
        deviceHelper.configureDevice(isStartRecording = false)
        audioRecorder.cancelRecording()
    }

    /**
     * Очистить измененные настройки девайса, которые были применены для записи.
     */
    fun clearDeviceRecordConfig() {
        deviceHelper.configureDevice(isStartRecording = false)
    }

    /**
     * Очистить менеджер.
     */
    fun release() {
        deviceHelper.configureDevice(isStartRecording = false)
        audioRecorder.release()
    }
}