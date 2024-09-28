package ru.tensor.sbis.design.message_panel.recorder_common.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Вспомогательный класс для работы с разрешениями.
 *
 * @author vv.chekurda
 */
abstract class PermissionsHelper(private val activity: Activity) {

    /**
     * Разрешения, которые необходимо проверять и запрашивать.
     */
    protected abstract val permissions: Array<String>

    /**
     * Код запроса разрешений.
     */
    protected abstract val requestCode: Int

    /**
     * Выполнить действие [action], если имеются указанные разрешения [permissions],
     * если разрешений нет, то произойдет запрос по коду [requestCode].
     */
    fun withPermissions(action: () -> Unit) =
        withPermissions(permissions, requestCode, action)

    private fun withPermissions(
        permissions: Array<String>,
        requestCode: Int,
        action: () -> Unit
    ) {
        if (checkPermissions(permissions)) {
            action()
        } else {
            ActivityCompat.requestPermissions(activity, permissions, requestCode)
        }
    }

    private fun checkPermissions(permissions: Array<String>): Boolean {
        permissions.forEach {
            val hasPermission = ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) return false
        }
        return true
    }
}