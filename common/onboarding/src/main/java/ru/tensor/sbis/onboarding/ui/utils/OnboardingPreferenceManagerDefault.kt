package ru.tensor.sbis.onboarding.ui.utils

import android.content.SharedPreferences
import ru.tensor.sbis.verification_decl.login.LoginInterface
import javax.inject.Inject
import javax.inject.Named

/**
 * Константа для получения [SharedPreferences] в di.
 */
const val ONBOARDING_SHARED_PREFERENCES = "ONBOARDING_SHARED_PREFERENCES"

/**
 * Базавая реализация интерфейса [OnboardingPreferenceManager].
 *
 * @author as.chadov
 *
 * @param preferences экземпляр [SharedPreferences]
 */
internal class OnboardingPreferenceManagerDefault @Inject constructor(
    @Named(ONBOARDING_SHARED_PREFERENCES) private val preferences: SharedPreferences,
    private val loginInterface: LoginInterface?
) : OnboardingPreferenceManager {

    override fun saveEntrance() = saveState(COMPLETED_ONBOARDING_PREF_BY_UUID)

    override fun saveProcessed() = saveState(PROCESSED_ONBOARDING_PREF_BY_UUID)

    override fun restoreEntrance() = restoreState(COMPLETED_ONBOARDING_PREF_BY_UUID)

    override fun restoreProcessed() = restoreState(PROCESSED_ONBOARDING_PREF_BY_UUID)

    override fun restoreAnyEntrance(): Boolean =
        preferences.getStringSet(COMPLETED_ONBOARDING_PREF_BY_UUID, emptySet()).isNullOrEmpty().not()

    private fun saveState(key: String) {
        val personsSeenOnboarding = preferences.getStringSet(key, emptySet())
            .orEmpty()
            .toMutableSet()
            .apply { add(getEntranceUuid()) }
        preferences.edit()
            .putStringSet(key, personsSeenOnboarding)
            .apply()
    }

    private fun restoreState(key: String): Boolean {
        return preferences.getStringSet(key, emptySet())
            .orEmpty()
            .run { contains(getEntranceUuid()) }
    }

    private fun getEntranceUuid(): String = loginInterface?.let {
        it.getCurrentAccount()?.personId ?: it.getCurrentAccount()?.uuid.toString()
    }.orEmpty()

    private companion object {
        const val COMPLETED_ONBOARDING_PREF_BY_UUID = "COMPLETED_ONBOARDING_PREF_BY_UUID"
        const val PROCESSED_ONBOARDING_PREF_BY_UUID = "PROCESSED_ONBOARDING_PREF_BY_UUID"
    }
}