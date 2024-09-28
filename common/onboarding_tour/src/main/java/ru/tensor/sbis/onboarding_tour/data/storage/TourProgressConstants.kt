package ru.tensor.sbis.onboarding_tour.data.storage

import androidx.datastore.preferences.core.booleanPreferencesKey
import ru.tensor.sbis.verification_decl.onboarding_tour.data.OnboardingTour

/** @SelfDocumented */
internal class TourProgressConstants(name: OnboardingTour.Name, user: String = "") {
    private val userKey = if (user.isNotBlank()) "$UUID_DELIMITER$user" else ""

    val isCompletedKey = booleanPreferencesKey("COMPLETED_KEY_${name.value}$userKey")

    companion object {
        /** @SelfDocumented */
        const val UUID_DELIMITER = "_"
    }
}