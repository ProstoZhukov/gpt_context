package ru.tensor.sbis.design.message_panel.audio_recorder.recorder

import android.Manifest
import android.app.Activity
import ru.tensor.sbis.design.message_panel.recorder_common.utils.PermissionsHelper

/**
 * Вспомогательный класс для запроса разрешений для записи аудио.
 *
 * @author vv.chekurda
 */
class AudioPermissionsHelper(activity: Activity) : PermissionsHelper(activity) {
    override val permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
    override val requestCode: Int = AUDIO_RECORD_REQUEST_CODE
}

/**
 * Код запроса разрешений для записи аудио.
 */
const val AUDIO_RECORD_REQUEST_CODE = 4331