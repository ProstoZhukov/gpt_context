package ru.tensor.sbis.message_panel.recorder.datasource

import android.media.MediaRecorder
import ru.tensor.sbis.recorder.decl.RecorderService
import timber.log.Timber

/**
 * Обработчик информационных сообщений о записи по умолчанию
 *
 * @author vv.chekurda
 * Создан 8/6/2019
 */
internal class DefaultRecorderInfoListener(
    private val recorderService: RecorderService
) : MediaRecorder.OnInfoListener {

    override fun onInfo(recorder: MediaRecorder, what: Int, extra: Int) {
        when (what) {
            MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED,
            MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED -> recorderService.stopRecord()
            else -> Timber.w("Unexpected info message (what: %d, extra: %d)", what, extra)
        }
    }
}