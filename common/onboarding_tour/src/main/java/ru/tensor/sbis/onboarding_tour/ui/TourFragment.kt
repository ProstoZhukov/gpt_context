package ru.tensor.sbis.onboarding_tour.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull
import ru.tensor.sbis.onboarding_tour.OnboardingTourPlugin
import ru.tensor.sbis.onboarding_tour.R
import ru.tensor.sbis.onboarding_tour.ui.di.OnboardingTourControllerInjector
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour
import timber.log.Timber
import javax.inject.Inject

/**
 * Фрагмент тура онбординга.
 */
internal class TourFragment : Fragment(R.layout.onboarding_tour_fragment) {

    @Inject
    lateinit var tourControllerFactory: OnboardingTourControllerInjector

    private var isOrientationLocked: Boolean = false

    lateinit var view: TourViewImpl
        private set

    private var controller: TourController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isOrientationLocked = savedInstanceState?.getBoolean(ORIENTATION_KEY) ?: false
        if (!isOrientationLocked) {
            lockOrientation()
        }
        OnboardingTourPlugin.tourComponent.tourFragmentFactory()
            .create(this)
            .inject(this)
        controller = tourControllerFactory.inject(this) {
            view = TourViewImpl(it)
            view
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val themeId = requireContext().getDataFromAttrOrNull(R.attr.onboardingTourTheme, false)
            ?: R.style.OnboardingTourDefaultTheme
        val themeInflater = inflater.cloneInContext(ContextThemeWrapper(requireContext(), themeId))
        return super.onCreateView(themeInflater, container, savedInstanceState)
    }

    override fun onDestroy() {
        controller = null
        super.onDestroy()
        if (!isOrientationLocked) return
        if (isRemoving || parentFragment?.isRemoving == true || requireActivity().isFinishing) {
            requireActivity().requestedOrientation = SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ORIENTATION_KEY, isOrientationLocked)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun lockOrientation() {
        if (DeviceConfigurationUtils.isTablet(requireContext())) return
        try {
            isOrientationLocked = true
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    companion object {
        /**@SelfDocumented*/
        const val ARG_TOUR_NAME = "ARG_TOUR_NAME"

        /**@SelfDocumented*/
        private const val ORIENTATION_KEY = "ORIENTATION_KEY"

        /**
         * Создать фрагмент для Приветственного экрана настроект приложения [tourName].
         */
        fun newInstance(tourName: OnboardingTour.Name): Fragment =
            TourFragment().withArgs {
                putParcelable(ARG_TOUR_NAME, tourName)
            }
    }
}