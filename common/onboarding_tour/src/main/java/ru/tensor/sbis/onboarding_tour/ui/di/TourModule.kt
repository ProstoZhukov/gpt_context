package ru.tensor.sbis.onboarding_tour.ui.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStoreFactory
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import ru.tensor.sbis.android_ext_decl.BuildConfig
import ru.tensor.sbis.onboarding_tour.domain.DispatcherProvider
import ru.tensor.sbis.onboarding_tour.ui.TourFragment
import ru.tensor.sbis.onboarding_tour.ui.TourFragment.Companion.ARG_TOUR_NAME
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourProvider
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour

/**
 * @author as.chadov
 */
@Suppress("unused")
@Module
internal class TourModule {

    @Provides
    fun provideTourName(fragment: TourFragment): OnboardingTour.Name =
        fragment.arguments?.getParcelable(ARG_TOUR_NAME) ?: OnboardingTourProvider.Companion.DEFAULT_NAME

    @Provides
    fun provideStoreFactory(): StoreFactory =
        if (BuildConfig.DEBUG) LoggingStoreFactory(TimeTravelStoreFactory()) else DefaultStoreFactory()

    @IoDispatcher
    @Provides
    fun providesIoDispatcher(dispatcherProvider: DispatcherProvider): CoroutineDispatcher = dispatcherProvider.io
}