package ru.tensor.sbis.onboarding_tour.ui.utils

import androidx.fragment.app.Fragment
import ru.tensor.sbis.onboarding_tour.ui.TourFragment

/**
 * Является ли фрагмент онбордингом.
 *
 * @author ps.smirnyh
 */
fun Fragment.isOnboardingFragment() = this is TourFragment