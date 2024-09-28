package ru.tensor.sbis.verification_decl.onboarding_tour.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Интерфейс тура по экранам онбординга и настроек.
 *
 * @author as.chadov
 */
interface OnboardingTour {

    /** @SelfDocumented */
    @Parcelize
    class Name(val value: String) : Parcelable
}