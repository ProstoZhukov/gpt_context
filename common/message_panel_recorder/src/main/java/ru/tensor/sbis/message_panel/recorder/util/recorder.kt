/**
 * Инструменты для подготовки и настройки звукозаписи
 *
 * @author vv.chekurda
 * Создан 8/6/2019
 */
package ru.tensor.sbis.message_panel.recorder.util

import android.app.Activity
import android.content.Context
import android.media.MediaRecorder
import android.media.MicrophoneDirection
import android.net.Uri
import android.os.Build
import ru.tensor.sbis.common.util.FileUtil
import ru.tensor.sbis.common_attachments.Attachment
import ru.tensor.sbis.design.swipeback.SwipeBackLayout
import ru.tensor.sbis.message_panel.recorder.RECORDER_MAX_DURATION
import ru.tensor.sbis.message_panel.recorder.RECORDER_MAX_FILE_SIZE
import ru.tensor.sbis.message_panel.recorder.RecorderViewImpl
import ru.tensor.sbis.message_panel.recorder.datasource.DefaultRecorderErrorListener
import ru.tensor.sbis.message_panel.recorder.datasource.RecorderServiceImpl
import ru.tensor.sbis.message_panel.recorder.datasource.file.RecordFileFactory
import ru.tensor.sbis.message_panel.recorder.datasource.file.RecordFileFactoryImpl
import ru.tensor.sbis.message_panel.recorder.permission.RecordPermissionMediatorImpl
import ru.tensor.sbis.message_panel.recorder.util.listener.DefaultRecorderViewListener
import ru.tensor.sbis.message_panel_recorder.R
import ru.tensor.sbis.recorder.decl.*
import timber.log.Timber

/**
 * Подготовка зависимостей для [RecorderView]
 */
fun createRecorderDependency(context: Context, activity: Activity, swipeBackLayout: SwipeBackLayout?) =
    object : RecorderViewDependency {
        override val recorderService: RecorderService =
            RecorderServiceImpl(createFileFactory(activity), DefaultRecorderErrorListener(activity))

        override val permissionMediator: RecordPermissionMediator =
            RecordPermissionMediatorImpl(activity)

        override val recordingListener: RecorderViewListener =
            DefaultRecorderViewListener(activity, swipeBackLayout)

        override val recorderView: RecorderView by lazy {
            RecorderViewImpl(context)
        }
    }

/**
 * Метод подготовки [MediaRecorder] для записи аудиосообщения
 */
internal fun MediaRecorder.init() = this.apply {
    setAudioSource(MediaRecorder.AudioSource.MIC)
    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

    setMaxFileSize(RECORDER_MAX_FILE_SIZE)
    setMaxDuration(RECORDER_MAX_DURATION)


    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
        if (!setPreferredMicrophoneDirection(MicrophoneDirection.MIC_DIRECTION_TOWARDS_USER)) {
            Timber.w("Unable to set proffered microphone direction")
        }
    }
}

/**
 * Создание аудио вложения
 *
 * @throws IllegalArgumentException если uri схема не файл
 */
internal fun createAudioAttachment(uri: Uri) =
    if (uri.scheme != "file")
        throw IllegalArgumentException("Only file scheme supported $uri")
    else
        Attachment(uri.toString(), uri.lastPathSegment!!, FileUtil.FileType.AUDIO, null)

/**
 * Подготовка [RecordFileFactory] для кэша
 */
internal fun createFileFactory(context: Context): RecordFileFactory = RecordFileFactoryImpl(
    context.getString(R.string.media_audio_message_file_name_template),
    context.cacheDir.path
)