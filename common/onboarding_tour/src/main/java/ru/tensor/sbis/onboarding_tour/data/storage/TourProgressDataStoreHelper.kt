package ru.tensor.sbis.onboarding_tour.data.storage

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import ru.tensor.sbis.onboarding_tour.data.storage.TourProgressConstants.Companion.UUID_DELIMITER
import ru.tensor.sbis.onboarding_tour.di.TourComponentScope
import javax.inject.Inject

/** @SelfDocumented */
@TourComponentScope
internal class TourProgressDataStoreHelper @Inject constructor(
    context: Context
) : TourProgressDataStore {

    private val Context.dataStore by preferencesDataStore(
        name = STORE_NAME
    )
    private val dataSource = context.dataStore

    override suspend fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): T {
        val prefs = dataSource.data.first()
        return prefs[key] ?: prefs.findUniqueEntranceByUserKey(key) ?: defaultValue
    }

    private fun <T> Preferences.findUniqueEntranceByUserKey(key: Preferences.Key<T>): T? {
        val oldKey = asMap().keys.firstOrNull {
            it.name.substringBeforeLast(UUID_DELIMITER) == key.name
        } ?: return null
        @Suppress("UNCHECKED_CAST")
        return this[oldKey] as T?
    }

    override suspend fun <T> putPreference(key: Preferences.Key<T>, value: T) {
        dataSource.edit { preferences ->
            preferences[key] = value
        }
    }

    private companion object {
        const val STORE_NAME = "TourProgressDataStore"
    }
}