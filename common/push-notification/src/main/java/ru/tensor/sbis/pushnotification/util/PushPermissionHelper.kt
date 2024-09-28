package ru.tensor.sbis.pushnotification.util

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build

/**
 * Хелпер для получения разрешения на показ пуш-уведомлений.
 * Использовать при отсутствии в приложении MainScreenWidget.
 *
 * @author am.boldinov
 */
object PushPermissionHelper {

    private var requested = false

    /**
     * Отправляет пользователю запрос на получение разрешений на показ пуш-уведомлений.
     * Показывает запрос только 1 раз.
     */
    @JvmStatic
    fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !requested) {
            requested = true
            val permissions = PushStartupPermissionProvider().permissions
            if (permissions.isNotEmpty()) {
                val candidates = mutableListOf<String>()
                permissions.forEach {
                    it.names.forEach { name ->
                        if (activity.checkSelfPermission(name) != PackageManager.PERMISSION_GRANTED
                            && !activity.shouldShowRequestPermissionRationale(name)
                        ) {
                            candidates.add(name)
                        }
                    }
                }
                if (candidates.isNotEmpty()) {
                    activity.requestPermissions(candidates.toTypedArray(), 1)
                }
            }
        }
    }
}