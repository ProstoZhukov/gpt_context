package ru.tensor.sbis.base_app_components

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.design.change_theme.contract.IgnoreStatusBarAutoColor
import ru.tensor.sbis.entrypoint_guard.activity.EntryPointActivity
import ru.tensor.sbis.login.common.AuthCommonPlugin
import ru.tensor.sbis.login.common.utils.QrAuthIntentHelper
import ru.tensor.sbis.verification_decl.auth.AuthAware
import ru.tensor.sbis.verification_decl.auth.AuthAware.CheckAuthStrategy
import ru.tensor.sbis.verification_decl.lockscreen.data.NextLaunchScreen.LOCK
import ru.tensor.sbis.verification_decl.lockscreen.data.NextLaunchScreen.LOGIN
import ru.tensor.sbis.verification_decl.lockscreen.data.NextLaunchScreen.MAIN
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.version_checker_decl.InstallationComponent

/**
 * Роутер для всех внешних интентов, показывает сплэш экран "с птичкой" если приложение открывается первый раз.
 * В манифесте обязательно декларировать для неё intent-filter для внешних неявных намерений, например ACTION_MAIN, ACTION_VIEW, ACTION_SEND и др.
 *
 * Для корректной обработки исключений инициализации контроллера обращение в наследнике [BaseLaunchActivity] к методам
 * микросервисов не должно происходить при инициализации класса, а только "лениво".
 *
 * Используйте [ignoreAuthState], если авторизацию рисует главная активность. Если true, сразу переходим на главную активность.
 *
 * @author du.bykov
 */
@SuppressLint("CustomSplashScreen")
abstract class BaseLaunchActivity : EntryPointActivity(), AuthAware, InstallationComponent, IgnoreStatusBarAutoColor {

    protected abstract val loginInterface: LoginInterface
    protected abstract val loginActivityClass: Class<out Activity?>?
    protected abstract val mainActivityClass: Class<out Activity?>?
    protected abstract val forceUpdateIntent: Intent?
    protected open val ignoreAuthState = false
    override var launchedForInstallation = false

    override fun onCreate(activity: AppCompatActivity, parent: FrameLayout, savedInstanceState: Bundle?) {
        if (shouldFinishActivity()) {
            finish()
            overridePendingTransition(0, 0)
            return
        }
        if (launchedForInstallation) {
            finish()
            return
        }

        checkForceUpdateAndLaunch() && return

        activity.lifecycle.addObserver(object : DefaultLifecycleObserver{
            override fun onResume(owner: LifecycleOwner) {
                lifecycleScope.launch(Dispatchers.IO) {
                    // Делаем задержку, чтобы успевал показываться сплеш-экран
                    delay(1000L)
                    withContext(Dispatchers.Main) {
                        goToNextScreen()
                    }
                }
            }
        })
    }

    private fun checkForceUpdateAndLaunch(): Boolean {
        if (forceUpdateIntent == null) return false

        startActivity(forceUpdateIntent)
        finish()
        overridePendingTransition(0, 0)
        return true
    }

    private fun goToNextScreen() {
        if (ignoreAuthState) {
            goToMainContentScreen()
        } else {
            when (loginInterface.nextLaunchScreen()) {
                MAIN -> goToMainContentScreen()
                LOGIN -> goToLoginScreen()
                LOCK -> goToLockScreen()
            }
        }
    }

    override val checkAuthStrategy = CheckAuthStrategy.Skip

    private fun goToLoginScreen() {
        // данные с сессионным токеном не требуются на главном экране МП
        if (!QrAuthIntentHelper.isIntentWithSessionToken(intent)) {
            AuthCommonPlugin.pendingDeepLinkFeature?.get()?.linkOpenerPendingLinkFeature?.saveLink(
                this,
                intent
            )
        }
        startActivity(buildIntent(loginActivityClass!!))
        finish()
    }

    private fun goToMainContentScreen() {
        // Данные с сессионным токеном не требуются на главном экране МП, если он не содержит авторизацию.
        if (!ignoreAuthState) {
            QrAuthIntentHelper.extractSessionToken(intent)
        }
        startActivity(buildIntent(mainActivityClass!!))
        finish()
    }

    private fun goToLockScreen() {
        val sourceIntent = buildIntent(mainActivityClass!!)
        val intent = loginInterface.createLockScreenIntent(this, sourceIntent)
        startActivity(intent)
        finish()
    }

    // fix bug that appears after you click launcher icon of just installed app
    private fun shouldFinishActivity(): Boolean {
        if (isTaskRoot) {
            return false
        }
        val intent = intent
        val action = intent.action
        return (intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                && action != null && action == Intent.ACTION_MAIN)
    }

    /**
     * Используется для роутинга внешнего неявного намерения в MainActivity, т.к. она знает что с ним делать.
     * Необходимо для того, чтобы при отрытии внешней ссылки из разлогиненнного приложения не попадать сразу в MainActivity,
     * фикс https://online.sbis.ru/opendoc.html?guid=e63d3e63-f243-45b1-b59e-9e562e1fba0c
     * Не используется копирование исходного намерения, причина:
     * https://developer.android.com/reference/android/os/strictmode/UnsafeIntentLaunchViolation
     *
     * @return копию полученного Intent, например при клике по ссылке снаружи
     */
    private fun buildIntent(component: Class<out Activity>): Intent {
        val newIntent = Intent(this, component)
        val sourceIntent = intent ?: return newIntent
        return newIntent.apply {
            action = sourceIntent.action
            data = sourceIntent.data
            for (cat in sourceIntent.categories.orEmpty()) {
                addCategory(cat)
            }
            flags = sourceIntent.flags
            sourceBounds = sourceIntent.sourceBounds
            putExtras(sourceIntent)
        }
    }
}