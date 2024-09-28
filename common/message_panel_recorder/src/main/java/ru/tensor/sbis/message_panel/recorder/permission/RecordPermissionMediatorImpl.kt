package ru.tensor.sbis.message_panel.recorder.permission

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ru.tensor.sbis.recorder.decl.RecordPermissionMediator

internal const val RECORDER_VIEW_PERMISSION = Manifest.permission.RECORD_AUDIO
internal const val RECORDER_VIEW_PERMISSION_REQUEST_CODE = 53034

/**
 * @author vv.chekurda
 * Создан 8/7/2019
 */
internal class RecordPermissionMediatorImpl(
    private val activity: Activity
) : RecordPermissionMediator {

    override fun withPermission(block: () -> Unit) {
        if (ContextCompat.checkSelfPermission(activity, RECORDER_VIEW_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            block()
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(RECORDER_VIEW_PERMISSION), RECORDER_VIEW_PERMISSION_REQUEST_CODE)
        }
    }
}