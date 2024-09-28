package ru.tensor.sbis.onboarding_tour.data.storage

import androidx.datastore.preferences.core.Preferences

/** @SelfDocumented */
internal interface TourProgressDataStore {

    suspend fun <T> putPreference(key: Preferences.Key<T>, value: T)

    suspend fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): T
}