package ru.tensor.sbis.message_panel.recorder.datasource

import android.content.Context
import android.media.MediaRecorder
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.message_panel_recorder.R
import timber.log.Timber

/**
 * Обработчик ошибок записи по умолчанию
 *
 * @author vv.chekurda
 * Создан 8/6/2019
 */
internal class DefaultRecorderErrorListener(
    private val context: Context
) : MediaRecorder.OnErrorListener {

    override fun onError(recorder: MediaRecorder, what: Int, extra: Int) {
        SbisPopupNotification.pushToast(context, R.string.media_recorder_starting_error)
        Timber.e("Error occurred during message recording (what: %d, extra: %d)", what, extra)
    }
}