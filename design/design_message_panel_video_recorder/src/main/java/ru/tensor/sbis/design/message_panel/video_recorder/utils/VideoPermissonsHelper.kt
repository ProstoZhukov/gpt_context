package ru.tensor.sbis.design.message_panel.video_recorder.utils

import android.Manifest
import android.app.Activity
import ru.tensor.sbis.design.message_panel.recorder_common.utils.PermissionsHelper

/**
 * Вспомогательная реализация для получения разрешений для записи видео.
 *
 * @author vv.chekurda
 */
internal class VideoPermissionsHelper(activity: Activity) : PermissionsHelper(activity) {
    override val permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA)
    override val requestCode: Int = VIDEO_RECORD_REQUEST_CODE
}

/**
 * Код запроса разрешений для записи видео.
 */
const val VIDEO_RECORD_REQUEST_CODE = 4431