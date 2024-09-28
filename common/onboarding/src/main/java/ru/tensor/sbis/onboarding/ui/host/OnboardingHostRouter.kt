package ru.tensor.sbis.onboarding.ui.host

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Browser
import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.application_tools.DebugTools
import ru.tensor.sbis.design_dialogs.dialogs.container.Container
import ru.tensor.sbis.mvvm.fragment.CommandRunner
import ru.tensor.sbis.onboarding.contract.impl.OnboardingNavigatorManager
import ru.tensor.sbis.onboarding.di.HostScope
import ru.tensor.sbis.onboarding.domain.event.*
import ru.tensor.sbis.onboarding.ui.OnboardingActivity
import ru.tensor.sbis.onboarding.ui.base.OnboardingNextPage
import ru.tensor.sbis.onboarding.ui.utils.OnboardingPreferenceManager
import ru.tensor.sbis.onboarding.ui.utils.OnboardingProviderMediator
import ru.tensor.sbis.onboarding.ui.utils.getParentFragmentAs
import ru.tensor.sbis.onboarding.ui.utils.hideScreen
import ru.tensor.sbis.onboarding.ui.utils.showScreen
import timber.log.Timber
import javax.inject.Inject

/**
 * Роутер приветственного экрана
 *
 * @param navigatorManager менеджер навигаци по фрагментам
 * @param navigationHandler фрагмент реализующий [OnboardingNextPage]
 * @param providerMediator класс для работы с набором провайдеров onboarding
 * @param preferenceManager класс выполняющий сохранение и восстановление настроек
 */
@HostScope
internal class OnboardingHostRouter @Inject constructor(
    fragment: Fragment,
    private val navigatorManager: OnboardingNavigatorManager,
    private val navigationHandler: OnboardingNextPage,
    private val providerMediator: OnboardingProviderMediator,
    private val preferenceManager: OnboardingPreferenceManager
) : CommandRunner() {

    init {
        fragment.lifecycle.addObserver(object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            private fun register() {
                resume(fragment, fragment.childFragmentManager)
                isResumed = true
                disposable = navigatorManager.observeHostTransitions()
                    .subscribe {
                        processTransaction(it)
                    }
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            private fun unregister() {
                pause()
                isResumed = false
                disposable?.dispose()
            }
        })
    }

    /**
     * Начинаем работу и прекращаем использование приветственного экрана
     *
     * @intent возможное запускаемое намерение
     */
    fun dismiss(intent: Intent) = runCommandSticky { fragment, _ ->
        with(fragment) {
            val activity = requireActivity()
            val parentFragment = getParentFragmentAs<Container.Closeable>()
            providerMediator.getActiveProvider()
                ?.getCustomOnboardingPreferenceManger()
                ?.saveProcessed()
                ?: preferenceManager.saveProcessed()
            navigatorManager.onOnboardingCloseEvent()
            when {
                /** открытие или возврат к основной активности [intent] если онбординг используется через собственную активность */
                activity is OnboardingActivity -> activity.processIntent(intent)
                // закрытие контейнера с онбордингом (планшет)
                parentFragment != null -> parentFragment.closeContainer()
                // удаление фрагмента с онбордингом если он не единственный в стеке
                // и информирование через [OnboardingNavigatorManager] (телефон)
                else -> {
                    fragment.parentFragmentManager.run {
                        val onboardingTag = OnboardingHostFragmentImpl.ONBOARDING_TAG
                        if (backStackEntryCount > 1 && onboardingTag == getBackStackEntryAt(0).name) {
                            Handler(Looper.getMainLooper()).post(::popBackStackImmediate)
                        } else {
                            navigatorManager.onDismissOnboarding()
                        }
                    }
                }
            }
        }
    }

    /**
     * Перелистываем страницу приветственного экрана
     */
    fun turnPage() {
        if (isResumed) {
            navigationHandler.goNextPage(true)
        }
    }

    /**
     * Открывает ссылку электронного ресурса для просмотра
     *
     * @url ссылка электронного ресурса
     */
    fun openLink(url: String) = runCommand { fragment, _ ->
        fragment.requireContext()
            .run {
                try {
                    startActivity(addLinkToIntent(this, url))
                } catch (e: ActivityNotFoundException) {
                    Timber.d("Attempt to open invalid link")
                }
            }
    }

    private fun Activity.processIntent(intent: Intent) {
        if (!DebugTools.isAutoTestLaunch) {
            intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)
            startActivity(intent)
        }
        finish()
    }

    private fun addLinkToIntent(
        context: Context,
        url: String,
    ) = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
            .normalizeScheme()
        putExtra(Browser.EXTRA_APPLICATION_ID, context.packageName)
    }

    private fun processTransaction(event: NavigateEvent) {
        when (event) {
            is NavigateForwardEvent -> navigationHandler.goNextPage()
            is NavigateBackwardEvent -> navigationHandler.goPreviousPage()
            is OpenCustomScreen -> event.run { openCustomScreen(creator, screenKey, containerId) }
            is DismissCustomScreen -> closeCustomScreen(event.screenKey)
        }
    }

    private fun openCustomScreen(
        creator: () -> Fragment,
        screenKey: String,
        @IdRes containerId: Int = ID_NULL,
    ) = runCommand { fragment, _ ->
        val isForeignActivity = fragment.requireActivity() !is OnboardingActivity
        if (containerId == ID_NULL && isForeignActivity) {
            return@runCommand
        }
        getActivityManager(fragment)?.run {
            val targetContainerId = if (containerId == ID_NULL) {
                OnboardingActivity.containerId
            } else {
                containerId
            }
            showScreen(
                tag = screenKey,
                getInstanceAction = creator,
                containerId = targetContainerId
            )
        }
    }

    private fun closeCustomScreen(
        screenKey: String,
    ) = runCommand { fragment, _ ->
        getActivityManager(fragment)?.run {
            hideScreen(screenKey)
        }
    }

    private fun getActivityManager(fragment: Fragment): FragmentManager? {
        val activity = fragment.requireActivity()
        return activity.supportFragmentManager
    }

    private var disposable: Disposable? = null
    private var isResumed: Boolean = false
}