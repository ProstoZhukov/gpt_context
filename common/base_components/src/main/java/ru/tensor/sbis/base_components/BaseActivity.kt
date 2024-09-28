package ru.tensor.sbis.base_components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import ru.tensor.sbis.android_ext_decl.AndroidComponent
import ru.tensor.sbis.base_components.util.disableAutofillServiceApi29
import ru.tensor.sbis.base_components.util.updateFullScreen
import ru.tensor.sbis.common.util.ContextReplacer
import ru.tensor.sbis.common.util.theme.ApplicationThemeHelper
import ru.tensor.sbis.design.swipeback.SwipeBackActivity
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard
import timber.log.Timber

/**
 * Базовый класс активити
 * Created by da.rodionov on 07.05.15.
 */
@UiThread
abstract class BaseActivity : SwipeBackActivity(), AndroidComponent, EntryPointGuard.LegacyEntryPoint {
    /**@SelfDocumented*/
    open val needToHotChangeTheme get() = false
    private var themeResId = 0

    /**@SelfDocumented*/
    protected var isRunning = true
        private set

    /**@SelfDocumented*/
    @Suppress("MemberVisibilityCanBePrivate")
    protected var isActivityResumed = false
        private set

    override fun attachBaseContext(base: Context?) {
        if (this is EntryPointGuard.EntryPoint) {
            EntryPointGuard.activityAssistant.interceptAttachBaseContext(this, base) {
                super.attachBaseContext(it)
            }
        } else {
            EntryPointGuard.activityAssistant.interceptAttachBaseContextLegacy(
                this,
                ContextReplacer.replace(base)
            ) {
                super.attachBaseContext(it)
            }
        }
    }

    final override fun onUserInteraction() {
        EntryPointGuard.activityAssistant.interceptOnUserInteraction(this) { super.onUserInteraction() }
    }

    final override fun onUserLeaveHint() {
        EntryPointGuard.activityAssistant.interceptOnUserLeaveHint(this) { super.onUserLeaveHint() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setCurrentTheme()
        super.onCreate(savedInstanceState)
        updateFullscreen()
        disableAutofillServiceApi29()
    }

    protected open fun updateFullscreen() {
        updateFullScreen()
    }

    @CallSuper
    override fun onStart() {
        invalidateThemeAndRestart()
        super.onStart()
        isRunning = true
    }

    @CallSuper
    override fun onResume() {
        invalidateThemeAndRestart()
        isActivityResumed = true
        super.onResume()
    }

    @CallSuper
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        isRunning = true
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        isActivityResumed = false
    }

    @CallSuper
    override fun onStop() {
        super.onStop()
        isRunning = false
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateFullscreen()
    }

    override fun getActivity(): Activity {
        return this
    }

    override fun getFragment(): Fragment? {
        return null
    }

    override fun setTheme(resid: Int) {
        themeResId = resid
        super.setTheme(resid)
    }

    private fun setCurrentTheme() {
        if (needToHotChangeTheme)
            (application as? ApplicationThemeHelper)?.let {
                if (themeResId != it.currentTheme)
                    setTheme(it.currentTheme)
            }
    }

    /**@SelfDocumented*/
    @Suppress("MemberVisibilityCanBePrivate")
    fun invalidateThemeAndRestart() {
        if (needToHotChangeTheme)
            (application as? ApplicationThemeHelper)?.let {
                if (themeResId != it.currentTheme) {
                    recreate()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            }
    }

    /**
     * Показать [Toast] уведомление
     *
     * @param [messageResId] ссылка на строковый ресурс с текстом сообщения
     * @param [length] длительность показа уведомления, по-умолчанию [Toast.LENGTH_LONG]
     */
    @JvmOverloads
    protected open fun showToast(@StringRes messageResId: Int, length: Int = Toast.LENGTH_LONG) {
        if (messageResId != 0) {
            showToast(applicationContext.getString(messageResId), length)
        } else {
            Timber.e("messageResId = 0")
        }
    }

    /**
     * Показать [Toast] уведомление.
     *
     * @param [message] текст сообщения
     * @param [length] длительность показа уведомления, по-умолчанию [Toast.LENGTH_LONG]
     */
    @JvmOverloads
    protected open fun showToast(message: CharSequence, length: Int = Toast.LENGTH_LONG) {
        SbisPopupNotification.pushToast(applicationContext, message)
    }
}
