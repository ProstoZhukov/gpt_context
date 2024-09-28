package ru.tensor.sbis.onboarding.contract.impl

import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.Gravity
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.tensor.sbis.android_ext_decl.BuildConfig
import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.application_tools.DebugTools
import ru.tensor.sbis.common.util.hasFragmentOrPendingTransaction
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.TabletContainerDialogFragment
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.VisualParamsBuilder
import ru.tensor.sbis.onboarding.ui.OnboardingActivity
import ru.tensor.sbis.onboarding.ui.host.OnboardingHostFragmentImpl
import ru.tensor.sbis.onboarding.ui.utils.OnboardingPreferenceManager
import ru.tensor.sbis.onboarding.ui.utils.OnboardingPreferenceManagerDefault
import ru.tensor.sbis.onboarding.ui.utils.OnboardingProviderMediator
import ru.tensor.sbis.onboarding.ui.utils.ThemeProvider
import ru.tensor.sbis.verification_decl.onboarding.OnboardingFeature
import ru.tensor.sbis.verification_decl.onboarding.OnboardingNavigator
import javax.inject.Inject

/**
 * Реализация [OnboardingFeature]
 *
 * @param preferenceManager класс выполняющий сохранение и восстановление настроек
 * @param intentProvider провайдер интента MainActivity
 * @param navigator навигатор по фрагментам
 * @param onboardingMediator класс для работы с набором провайдеров onboarding
 *
 * @author as.chadov
 */
internal class OnboardingFeatureImpl @Inject constructor(
    private val context: Context,
    private val preferenceManager: OnboardingPreferenceManager,
    private val intentProvider: MainActivityProvider,
    private val navigator: NavigatorManagerImpl,
    private val onboardingMediator: OnboardingProviderMediator,
    private val ioDispatcher: CoroutineDispatcher
) : OnboardingFeature {

    override val action: String = ONBOARDING_ACTIVITY

    override fun substituteIntent(
        origin: Intent?,
        ignoreTablet: Boolean
    ): Intent = if (isOnboardingShown() || (isTablet() && ignoreTablet)) {
        origin ?: intentProvider.getMainActivityIntent()
    } else {
        getOnboardingActivityIntent()
    }

    override fun getOnboardingActivityIntent() =
        Intent(context, OnboardingActivity::class.java).apply {
            action = ONBOARDING_ACTIVITY
            setPackage(BuildConfig.MAIN_APP_ID)
        }

    override fun startNonShownOnboarding(
        activity: Context?,
        ignoreTablet: Boolean,
        dialogOnTablet: Boolean
    ): Boolean =
        !isOnboardingShown() && (!ignoreTablet || !isTablet()) && run {
            if (isTablet() && dialogOnTablet) {
                if (activity is FragmentActivity) {
                    showOnboardingDialogFragment(activity.supportFragmentManager)
                    true
                } else false
            } else {
                val intent = getOnboardingActivityIntent()
                if (activity == null && context is Application) {
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(activity ?: context, intent, null)
                true
            }
        }

    override fun getOnboardingFragment(): Fragment = OnboardingHostFragmentImpl.newInstance()

    override fun getOnboardingDialogFragment() =
        TabletContainerDialogFragment()
            .setInstant(true)
            .setCancelableContainer(false)
            .setVisualParams(
                VisualParamsBuilder()
                    .gravity(Gravity.CENTER)
                    .setBoundingRectFromTargetFragment()
                    .build()
            )
            .setContentCreator(OnboardingHostFragmentImpl.Creator())

    override fun showOnboardingDialogFragment(manager: FragmentManager) {
        if (!manager.hasFragmentOrPendingTransaction(ONBOARDING_DIALOG_CONTAINER_TAG)) {
            getOnboardingDialogFragment().show(manager, ONBOARDING_DIALOG_CONTAINER_TAG)
        }
    }

    override fun getOnboardingFragmentStackTag(): String = OnboardingHostFragmentImpl.ONBOARDING_TAG

    override fun isOnboardingShown() = onboardingMediator.check() || DebugTools.isAutoTestLaunch

    override suspend fun isOnboardingTourShown(): Boolean = withContext(ioDispatcher) {
        if (preferenceManager is OnboardingPreferenceManagerDefault) {
            preferenceManager.restoreAnyEntrance()
        } else {
            false
        }
    }

    override suspend fun isOnboardingProcced(): Boolean = withContext(ioDispatcher) {
        onboardingMediator.getActiveProvider()
            ?.getCustomOnboardingPreferenceManger()
            ?.restoreProcessed()
            ?: preferenceManager.restoreProcessed()
    }

    override fun getOnboardingNavigator(): OnboardingNavigator = navigator

    override fun observeOnboardingDismiss(): Observable<Unit> = navigator.observeOnboardingDismiss()

    override fun observeOnboardingCloseEvent(): Observable<Unit> =
        navigator.observeOnboardingCloseEvent()

    private fun isTablet(): Boolean = ThemeProvider.isTablet(context)

    internal companion object {
        const val ONBOARDING_ACTIVITY = BuildConfig.MAIN_APP_ID + ".ONBOARDING_ACTIVITY"
        const val ONBOARDING_DIALOG_CONTAINER_TAG = "ONBOARDING_DIALOG_CONTAINER_TAG"
    }
}