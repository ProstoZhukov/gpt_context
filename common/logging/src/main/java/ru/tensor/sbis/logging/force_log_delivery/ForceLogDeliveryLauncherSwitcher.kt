package ru.tensor.sbis.logging.force_log_delivery

import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import timber.log.Timber

/**
 * Метод для включения/отключения лаунчера для экрана принудительной отправки логов
 * @param enabled true - включает, false - отключает
 */
internal fun Application.tryToSetForceLogDeliveryLauncherEnabled(enabled: Boolean) {

    try {
        packageManager.setComponentEnabledSetting(
            ComponentName(
                this,
                ForceLogDeliverySplashScreenActivity::class.java.canonicalName!!
            ),
            if (enabled) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    } catch (ex: IllegalArgumentException) {
        // [ForceLogDeliverySplashScreenActivity] может отсутствовать - тогда управлять им не нужно, игнорируем ошибку
        Timber.d("Экран принудительной отправки логов не найден")
    }
}