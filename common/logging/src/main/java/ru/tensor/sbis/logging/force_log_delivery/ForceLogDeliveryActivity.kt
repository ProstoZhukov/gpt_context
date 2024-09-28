package ru.tensor.sbis.logging.force_log_delivery

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import ru.tensor.sbis.base_components.BaseActivity
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard
import ru.tensor.sbis.logging.LoggingPlugin
import ru.tensor.sbis.logging.R
import ru.tensor.sbis.verification_decl.auth.AuthAware
import ru.tensor.sbis.verification_decl.auth.AuthAware.CheckAuthStrategy
import ru.tensor.sbis.verification_decl.auth.AuthAware.CheckAuthStrategy.Skip

/**
 * Activity c фрагментом для отправки логов на облако.
 * Кроме фрагмента не содержит ничего
 *
 * @author av.krymov
 */
class ForceLogDeliveryActivity : BaseActivity(), AuthAware, EntryPointGuard.LegacyEntryPoint {

    override val checkAuthStrategy: CheckAuthStrategy = Skip

    override fun attachBaseContext(base: Context?) {
        EntryPointGuard.activityAssistant.interceptAttachBaseContextLegacy(this, base) { super.attachBaseContext(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.logging_force_log_delivery_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.logging_fragment_container,
                    getForceLogDeliveryFragment(),
                    null
                )
                .commit()
        }
    }

    private fun getForceLogDeliveryFragment(): Fragment {
        return with(LoggingPlugin) {
            loggingComponent.getForceLogDeliveryScreenProvider()?.getForceLogDeliveryScreen()
                ?: loggingFeature.getLoggingFragmentProvider().getLoggingFragment()
        }
    }
}