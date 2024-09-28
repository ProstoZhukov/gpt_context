package ru.tensor.sbis.logging.force_log_delivery

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import ru.tensor.sbis.base_components.BaseActivity
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard
import ru.tensor.sbis.logging.LoggingPlugin

/**
 * Activity, которое запускается через лаучер принудительной отправки логов.
 * Кроме Splash Screen, не содержит ничего, просто запускает [ForceLogDeliveryActivity] и сразу закрывается
 *
 * @author av.krymov
 */
@SuppressLint("CustomSplashScreen")
class ForceLogDeliverySplashScreenActivity : BaseActivity(), EntryPointGuard.LegacyEntryPoint {

    override fun attachBaseContext(base: Context?) {
        EntryPointGuard.activityAssistant.interceptAttachBaseContextLegacy(this, base) { super.attachBaseContext(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (LoggingPlugin.customizationOptions.enableForceLogDeliveryLauncher) startActivity(
            Intent(
                this,
                ForceLogDeliveryActivity::class.java
            )
        )
        finish()
    }
}