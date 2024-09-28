package ru.tensor.sbis.base_components.activity.behaviour

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.common.util.theme.ApplicationThemeHelper
import ru.tensor.sbis.entrypoint_guard.activity.contract.ActivityBehaviour

/**
 * Реализация поведения "горячей" смены темы.
 * Активность будет пересоздана.
 *
 * @author kv.martyshenko
 */
class HotThemeReloadBehaviour(
    private val themeHelperProvider: (AppCompatActivity) -> ApplicationThemeHelper? = { activity ->
        activity.application as? ApplicationThemeHelper
    },
    private val themeProvider: (ApplicationThemeHelper) -> Int = { provider ->
        provider.currentTheme
    }
) : ActivityBehaviour<AppCompatActivity> {
    private var currentThemeId: Int? = null

    override fun onPreCreate(activity: AppCompatActivity) {
        val themeHelper = themeHelperProvider(activity) ?: return

        currentThemeId = themeProvider(themeHelper)
        activity.setTheme(currentThemeId!!)
    }

    override fun onCreate(activity: AppCompatActivity) {
        val themeHelper = themeHelperProvider(activity) ?: return

        fun invalidateThemeAndRestart() {
            if (currentThemeId != themeHelper.currentTheme) {
                activity.recreate()
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }

        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onStart(owner: LifecycleOwner) {
                invalidateThemeAndRestart()
            }

            override fun onResume(owner: LifecycleOwner) {
                invalidateThemeAndRestart()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                owner.lifecycle.removeObserver(this)
                super.onDestroy(owner)
            }
        })
    }

}