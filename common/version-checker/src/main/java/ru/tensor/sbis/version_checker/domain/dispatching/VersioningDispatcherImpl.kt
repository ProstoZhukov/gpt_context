package ru.tensor.sbis.version_checker.domain.dispatching

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import ru.tensor.sbis.application_tools.DebugTools
import ru.tensor.sbis.common.lifecycle.AbstractActivityLifecycleCallbacks
import ru.tensor.sbis.entrypoint_guard.EntryPointGuard
import ru.tensor.sbis.entrypoint_guard.activity.ActivityAssistant
import ru.tensor.sbis.version_checker.VersionCheckerPlugin
import ru.tensor.sbis.version_checker.domain.InstallerManager
import ru.tensor.sbis.version_checker.domain.VersionManager
import ru.tensor.sbis.version_checker_decl.InstallationComponent
import ru.tensor.sbis.version_checker_decl.VersionedComponent
import ru.tensor.sbis.version_checker_decl.VersionedComponent.Strategy.CHECK_CRITICAL
import ru.tensor.sbis.version_checker_decl.VersionedComponent.Strategy.CHECK_RECOMMENDED
import ru.tensor.sbis.version_checker_decl.VersioningDispatcher
import ru.tensor.sbis.version_checker_decl.VersioningDispatcher.Strategy
import ru.tensor.sbis.version_checker_decl.data.UpdateStatus
import kotlin.collections.set

/**
 * Реализация диспетчера [VersioningDispatcher].
 * Позволяет отслеживать события ЖЦ МП для периодического инициирования проверки версий.
 *
 * @property versionManager менеджер по работе с функционалом версионирования
 * @property watchersMap граф глубокого версионирования по фрагментам
 * @property runningStates мапа интерактивных активити
 *
 * @author as.chadov
 */
internal class VersioningDispatcherImpl :
    VersioningDispatcher,
    AbstractActivityLifecycleCallbacks() {

    private var behaviour = Strategy.REGULAR
    private var isRegistered = false
    private val runningStates = mutableMapOf<Int, Boolean>()
    private val watchersMap = mutableMapOf<Int, VersionedActivityLifecycleWatcher>()
    private val jobsMap = mutableMapOf<Int, Job>()
    private val versionManager: VersionManager by lazy { VersionCheckerPlugin.versioningComponent.versionManager }
    private val installerManager: InstallerManager by lazy { VersionCheckerPlugin.versioningComponent.installerManager }
    private val coroutineScope = MainScope() + CoroutineName("VersioningDispatcher")

    private val Activity.id get() = this::class.hashCode()

    /** Необходимо ли обрабатывать критическое версионирование для данной [Activity]. */
    private val Activity.isCriticalSupported: Boolean
        get() = when {
            DebugTools.isAutoTestLaunch                  -> false
            this::class.java.name.contains(LEAK_PACKAGE) -> false
            this is VersionedComponent                   -> versioningStrategy == CHECK_CRITICAL
            else                                          -> true
        }

    /** Необходимо ли обрабатывать рекомендуемое версионирование для данной [Activity]. */
    private val Activity.isRecommendedSupported: Boolean
        get() = when {
            DebugTools.isAutoTestLaunch                  -> false
            this::class.java.name.contains(LEAK_PACKAGE) -> false
            this is VersionedComponent                   -> versioningStrategy == CHECK_RECOMMENDED
            else                                          -> true
        }

    // region VersioningDispatcher

    /**
     * FIXME Временное решение для версионирования с учетом асинхронной инициализации.
     */
    override val onReadyInterceptor: ActivityAssistant.OnReadyInterceptor =
        object : ActivityAssistant.OnReadyInterceptor {

            override suspend fun <T> interceptOnReady(
                activity: T,
                parentView: FrameLayout,
                savedInstanceState: Bundle?,
                onReady: suspend (T, FrameLayout, Bundle?) -> Unit
            ) where T : AppCompatActivity, T : EntryPointGuard.EntryPoint {
                if (activity.launchedForInstallation()) {
                    activity.finish()
                    return
                }

                // Критическое
                if (versionManager.isApplicationCriticalIncompatibility()) {
                    goToCriticalScreen(activity)
                }
                activity.collectCriticalVersioning()

                // Рекомендательное
                activity.collectRecommendedVersioning()
                activity.initFragmentDispatching()

                onReady(activity, parentView, savedInstanceState)
            }

            override fun <T> interceptOnRestore(
                activity: T,
                parentView: FrameLayout,
                savedInstanceState: Bundle,
                onReady: (T, FrameLayout, Bundle?) -> Unit
            ) where T : AppCompatActivity, T : EntryPointGuard.EntryPoint =
                onReady(activity, parentView, savedInstanceState)
        }

    override fun isLaunchedForInstallation(activity: AppCompatActivity, intent: Intent?): Boolean =
        (intent ?: activity.intent)?.let {
            installerManager.handleInstallationCase(activity, it)
        } ?: false

    /**
     * Запуск диспетчера версионирования, подписка на события жизненного цикла активностей.
     */
    override fun start(application: Application) {
        if (!isRegistered) {
            isRegistered = true
            application.registerActivityLifecycleCallbacks(this)
        }
    }

    /**
     * Изменение поведения диспетчера версионирования.
     * @see [VersioningDispatcher.Strategy].
     */
    override fun behaviour(behaviour: Strategy): VersioningDispatcher {
        this.behaviour = behaviour
        return this
    }
    // endregion VersioningDispatcher

    // region AbstractActivityLifecycleCallbacks
    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        if (activity is InstallationComponent && activity.launchedForInstallation()) {
            activity.launchedForInstallation = true
        }
        super.onActivityCreated(activity, bundle)
        if (activity.isCriticalSupported) {
            if (versionManager.isApplicationCriticalIncompatibility()) {
                goToCriticalScreen(activity)
            }
            activity.collectCriticalVersioning()
        }
        if (activity.isRecommendedSupported && activity is FragmentActivity) {
            activity.collectRecommendedVersioning()
            activity.initFragmentDispatching()
        }
    }

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)
        if (activity !is ComponentActivity) {
            runningStates[activity.id] = true
        }
    }

    override fun onActivityStopped(activity: Activity) {
        super.onActivityStopped(activity)
        if (activity !is ComponentActivity) {
            runningStates[activity.id] = false
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        super.onActivityDestroyed(activity)
        jobsMap.remove(activity.id)?.cancel("Activity destroyed")
        activity.cancelFragmentDispatching()
    }
    // endregion AbstractActivityLifecycleCallbacks

    /** Подписаться на событие критического версонирования. */
    private fun Activity.collectCriticalVersioning() {
        if (this is ComponentActivity) {
            lifecycleScope.launch {
                repeatOnLifecycle(STARTED) {
                    versionManager.state.collect { status ->
                        if (status == UpdateStatus.Mandatory) {
                            goToCriticalScreen(this@collectCriticalVersioning)
                        }
                    }
                }
            }
        } else {
            jobsMap[id] = coroutineScope.launch {
                versionManager.state
                    .filter { it == UpdateStatus.Mandatory && runningStates[id] == true }
                    .collect {
                        goToCriticalScreen(this@collectCriticalVersioning)
                    }
            }
        }
    }

    /** Подписаться на событие рекомендуемого версонирования. */
    @OptIn(FlowPreview::class)
    private fun FragmentActivity.collectRecommendedVersioning() {
        if (behaviour == Strategy.BY_FRAGMENTS) return
        lifecycleScope.launch {
            repeatOnLifecycle(RESUMED) {
                // Добавил debounce так как в приложениях с асинхронной инициализацией, окно не всегда показывается
                // https://dev.sbis.ru/opendoc.html?guid=b2fb7961-8c47-4a9b-8a7b-f0401c06d21e&client=3
                versionManager.state.debounce(DELAY_BEFORE_SHOW_RECOMMENDATION)
                    .collect { status ->
                        if (status == UpdateStatus.Recommended) {
                            versionManager.showRecommendedFragment(supportFragmentManager)
                        }
                    }
            }
        }
    }

    /** Открыть экран критического обновления. */
    private fun goToCriticalScreen(activity: Activity) = activity.run {
        val criticalIntent = versionManager.getForcedUpdateAppActivityIntent(false)
        startActivity(criticalIntent)
        finish()
        overridePendingTransition(0, 0)
    }

    /** Экран открыт по qr-ссылке только для установки другого МП семейства Сбис. */
    private fun Activity.launchedForInstallation(): Boolean =
        intent?.let {
            installerManager.handleInstallationCase(this, it)
        } ?: false

    /** Иниицировать версионирование по фрагментам активити. */
    private fun FragmentActivity.initFragmentDispatching() {
        if (behaviour == Strategy.REGULAR) return
        watchersMap[id] = VersionedActivityLifecycleWatcher(activity = this)
    }

    /** Завершить версионирование по фрагментам активити. */
    private fun Activity.cancelFragmentDispatching() {
        if (behaviour == Strategy.BY_FRAGMENTS && this is FragmentActivity) {
            watchersMap.remove(id)?.let { dispatcher ->
                lifecycle.removeObserver(dispatcher)
            }
        }
    }

    private companion object {
        const val LEAK_PACKAGE = "leakcanary.internal"
        const val DELAY_BEFORE_SHOW_RECOMMENDATION = 1000L
    }
}