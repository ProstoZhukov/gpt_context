package ru.tensor.sbis.onboarding_tour.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourCreator
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourFragmentProvider
import ru.tensor.sbis.onboarding_tour.contract.OnboardingTourDependency
import ru.tensor.sbis.onboarding_tour.data.storage.TourProgressDataStore
import ru.tensor.sbis.onboarding_tour.ui.di.TourFragmentComponent
import ru.tensor.sbis.verification_decl.onboarding_tour.DevicePerformanceProvider
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourEventsProvider

/**
 * DI комопнент модуля Приветственного экрана и настроект приложения.
 *
 * @author as.chadov
 */
@TourComponentScope
@Component(modules = [OnboardingTourModule::class])
internal interface OnboardingTourComponent {

    /**@SelfDocumented */
    val creator: OnboardingTourCreator

    /**@SelfDocumented */
    val uiProvider: OnboardingTourFragmentProvider

    /**@SelfDocumented */
    val dataStore: TourProgressDataStore

    /**@SelfDocumented */
    val performanceProvider: DevicePerformanceProvider

    /**@SelfDocumented */
    val eventsProvider: OnboardingTourEventsProvider

    /**@SelfDocumented */
    val dependency: OnboardingTourDependency

    /**@SelfDocumented */
    fun tourFragmentFactory(): TourFragmentComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance dependency: OnboardingTourDependency
        ): OnboardingTourComponent
    }
}