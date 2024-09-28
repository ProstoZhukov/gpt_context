package ru.tensor.sbis.version_checker.domain

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import dagger.Lazy
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.tensor.sbis.verification_decl.login.event.ChangeHostEvent
import ru.tensor.sbis.verification_decl.login.event.InitHostEvent
import ru.tensor.sbis.version_checker.analytics.Analytics
import ru.tensor.sbis.version_checker.analytics.AnalyticsEvent
import ru.tensor.sbis.version_checker.contract.VersioningDependency
import ru.tensor.sbis.version_checker.data.Version
import ru.tensor.sbis.version_checker.data.VersioningSettingsHolder
import ru.tensor.sbis.version_checker.di.ManagerDispatcher
import ru.tensor.sbis.version_checker.di.VersioningSingletonScope
import ru.tensor.sbis.version_checker.domain.cache.DebugStateHolder
import ru.tensor.sbis.version_checker.domain.cache.VersioningLocalCache
import ru.tensor.sbis.version_checker.domain.debug.VersioningDebugTool
import ru.tensor.sbis.version_checker.domain.service.InAppUpdateChecker
import ru.tensor.sbis.version_checker.domain.service.VersionServiceChecker
import ru.tensor.sbis.version_checker.domain.utils.usePlayServiceRecommended
import ru.tensor.sbis.version_checker.domain.utils.useSbisCritical
import ru.tensor.sbis.version_checker.domain.utils.useSbisRecommended
import ru.tensor.sbis.version_checker.ui.mandatory.RequiredUpdateActivity
import ru.tensor.sbis.version_checker.ui.recommended.RecommendedUpdateFragment
import ru.tensor.sbis.version_checker.ui.settings.SettingsVersionsHostFragment
import ru.tensor.sbis.version_checker_decl.CriticalIncompatibilityProvider
import ru.tensor.sbis.version_checker_decl.IsActualVersionProvider
import ru.tensor.sbis.version_checker_decl.VersioningDebugActivator
import ru.tensor.sbis.version_checker_decl.VersioningInitializer
import ru.tensor.sbis.version_checker_decl.VersioningIntentProvider
import ru.tensor.sbis.version_checker_decl.data.UpdateStatus
import timber.log.Timber
import javax.inject.Inject

/**
 * Реализация менеджера проверки версий приложения и его модулей.
 *
 * @property settingsHolder холдер настроек версионирования
 * @property appVersion текущая версия установленного МП
 * @property outdatedOnGooglePlay true если в GooglePLay доступна новая версия
 * @property isInAsyncProgress true если идет синхронизация версии
 */
@VersioningSingletonScope
internal class VersionManager @Inject constructor(
    @ManagerDispatcher private val dispatcher: CoroutineDispatcher,
    appSettingsHolder: Lazy<VersioningSettingsHolder>,
    private val sbisVersionChecker: Lazy<VersionServiceChecker>,
    private val inAppUpdateChecker: Lazy<InAppUpdateChecker>,
    private val preferences: VersioningLocalCache,
    debugState: Lazy<DebugStateHolder>,
    private val analytics: Analytics,
    private val dependency: VersioningDependency
) : VersioningDebugTool,
    VersioningInitializer,
    VersioningIntentProvider,
    CriticalIncompatibilityProvider,
    IsActualVersionProvider,
    VersioningDebugActivator {

    private val settingsHolder: VersioningSettingsHolder by lazy { appSettingsHolder.get() }
    private val debugState: DebugStateHolder by lazy { debugState.get() }
    private val appVersion by lazy { Version(settingsHolder.appVersion) }
    private val managerScope = CoroutineScope(
        dispatcher + CoroutineName("VersionManager") + SupervisorJob() + CoroutineExceptionHandler { _, throwable ->
            Timber.w(throwable)
        }
    )
    private val _state: MutableStateFlow<UpdateStatus> = MutableStateFlow(UpdateStatus.Empty)
    private var outdatedOnGooglePlay = false
    private var isInAsyncProgress = false
    private var isRegistered = false

    /** Состояние статуса (типа) обновления. */
    val state: StateFlow<UpdateStatus> = _state

    /**
     * Запускает проверку текущей версии к рекомендуемой и минимально поддерживаемой.
     * Актуализирует удаленные настройки на устройстве запросом в сбис сервис и google play.
     */
    override fun init() {
        isRegistered && return
        isRegistered = true
        managerScope.launch {
            useSbisService()
        }
        managerScope.launch {
            useGooglePlayService()
        }
        managerScope.launch {
            checkCompatibility()
        }
    }

    /**
     * Проверка приложения на критическую несовместимость.
     */
    override fun isApplicationCriticalIncompatibility() =
        state.value == UpdateStatus.Mandatory

    /**
     * Проверка приложения на актуальность версии.
     */
    override fun isActualVersion() = state.value == UpdateStatus.Empty

    @SuppressLint("CheckResult")
    private suspend fun useSbisService() {
        if (settingsHolder.useSbisCritical() || settingsHolder.useSbisRecommended()) {
            dependency.loginInterfaceProvider
                ?.loginInterface
                ?.hostEventObservable
                ?.subscribe { hostEvent ->
                    managerScope.launch {
                        when (hostEvent) {
                            is InitHostEvent -> if (needUpdateRemoteSettings()) updateRemoteSettings()
                            is ChangeHostEvent -> updateRemoteSettings()
                        }
                    }
                }
        } else if (needUpdateRemoteSettings()) {
            updateRemoteSettings()
        }
    }

    private suspend fun useGooglePlayService() {
        if (settingsHolder.usePlayServiceRecommended()) {
            outdatedOnGooglePlay = inAppUpdateChecker.get().requestUpdateAvailable()
            if (outdatedOnGooglePlay) {
                checkCompatibility()
            }
        }
    }

    /** Проверить необходимость обновления удаленных настроек версионирования МП */
    private fun needUpdateRemoteSettings(): Boolean {
        return !isInAsyncProgress && preferences.isRemoteVersionSettingsExpired()
    }

    private suspend fun updateRemoteSettings() {
        if (isInAsyncProgress) {
            return
        }
        isInAsyncProgress = true
        sbisVersionChecker.get().update().collect { result ->
            result?.let {
                settingsHolder.update(it)
                preferences.saveDictionary(it)
                checkCompatibility()
            }
            isInAsyncProgress = false
        }
    }

    /**
     * Выполнить проверку версии МП на совместимость с удаленными (отладочными) настройками версионирования.
     * Должна выполняться:
     * - при старте приложения по последним закэшированным удаленным настройкам
     * - после обновления удаленных настроек с облака
     * - при включении отладки
     */
    private suspend fun checkCompatibility() {
        val status = if (incompatibleBy(UpdateStatus.Mandatory)) {
            debugState.resetDebugLock()
            UpdateStatus.Mandatory
        } else if (outdatedOnGooglePlay || incompatibleBy(UpdateStatus.Recommended)) {
            UpdateStatus.Recommended
        } else {
            return
        }
        _state.emit(status)
    }

    private fun incompatibleBy(status: UpdateStatus): Boolean {
        if (debugState.isModeOn && debugStatus != status) {
            return false
        }
        val installedVersion = if (debugState.isModeOn) debugState.getDebugVersion() else appVersion
        installedVersion.isUnspecified && return false
        val remoteVersion = settingsHolder.remoteVersionFor(status) ?: return false
        return installedVersion < remoteVersion
    }

    /** Инициировать отображение фрагмента рекомендованного обновления. */
    fun showRecommendedFragment(fragmentManager: FragmentManager) {
        !preferences.isRecommendationExpired() && return
        fragmentManager.isRecommendedDialogShown() && return
        preferences.postponeUpdateRecommendation(false)
        Looper.myQueue().addIdleHandler {
            RecommendedUpdateFragment.newInstance().show(fragmentManager, RecommendedUpdateFragment.screenTag)
            analytics.send(AnalyticsEvent.ShowRecommendedScreen())
            false
        }
    }

    private fun FragmentManager.isRecommendedDialogShown(): Boolean =
        findFragmentByTag(RecommendedUpdateFragment.screenTag)?.let {
            it is RecommendedUpdateFragment
        } ?: false

    // region VersioningIntentProvider
    override fun getForcedUpdateAppActivityIntent(ifObsolete: Boolean): Intent? =
        if (!ifObsolete || isApplicationCriticalIncompatibility()) {
            RequiredUpdateActivity.createIntent()
        } else {
            null
        }
    // endregion VersioningIntentProvider

    // region VersioningDebugActivator
    override fun createVersioningDebugFragment(
        withNavigation: Boolean,
        title: String?,
        showToolbar: Boolean
    ): Fragment = SettingsVersionsHostFragment.newInstance(
        title = title,
        showToolbar = showToolbar,
        withNavigation = withNavigation
    )
    // endregion VersioningDebugActivator

    //region VersioningDebugTool
    override val realVersion get() = appVersion.version
    override val debugVersion get() = debugState.getDebugVersion().version
    override val debugStatus get() = debugState.getUpdateDebugStatus()

    override fun applyUpdateStatus(status: UpdateStatus) = debugState.setUpdateDebugStatus(status)

    override fun applyDebugVersion(version: String) {
        debugState.setDebugVersion(version)
        managerScope.launch {
            delay(DEBUG_DELAY_MS)
            checkCompatibility()
        }
    }
    //endregion VersioningDebugTool

    internal companion object {
        const val DEBUG_DELAY_MS = 5000L
    }
}
