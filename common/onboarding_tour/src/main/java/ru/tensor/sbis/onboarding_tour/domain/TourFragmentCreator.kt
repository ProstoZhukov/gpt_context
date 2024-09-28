package ru.tensor.sbis.onboarding_tour.domain

import androidx.fragment.app.Fragment
import ru.tensor.sbis.application_tools.DebugTools
import ru.tensor.sbis.onboarding_tour.OnboardingTourPlugin
import ru.tensor.sbis.verification_decl.onboarding_tour.OnboardingTourFragmentProvider
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour
import ru.tensor.sbis.onboarding_tour.data.storage.TourStorage
import ru.tensor.sbis.onboarding_tour.di.TourComponentScope
import ru.tensor.sbis.onboarding_tour.ui.TourFragment
import java.lang.IllegalArgumentException
import javax.inject.Inject

/**
 * Реализация поставщика [OnboardingTourFragmentProvider].
 *
 * @author as.chadov
 */
@TourComponentScope
internal class TourFragmentCreator @Inject constructor(
    private val storage: TourStorage
) : OnboardingTourFragmentProvider {

    override suspend fun getNext(): Fragment? =
        storage.hasActiveTour()?.let(TourFragment.Companion::newInstance)

    override fun create(tourName: OnboardingTour.Name): Fragment =
        if (DebugTools.isAutoTestLaunch && !OnboardingTourPlugin.customizationOptions.allowInAutoTest) {
            throw IllegalStateException("Приветственный экран недоступен в режиме автотестов.")
        } else if (storage.hasTour(tourName)) {
            TourFragment.newInstance(tourName)
        } else {
            throw IllegalArgumentException("No ${OnboardingTour::class.java.simpleName} found named $tourName")
        }
}