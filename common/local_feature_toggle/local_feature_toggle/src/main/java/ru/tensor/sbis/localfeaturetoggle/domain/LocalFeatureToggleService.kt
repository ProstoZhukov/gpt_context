package ru.tensor.sbis.localfeaturetoggle.domain

import android.content.Context
import androidx.core.content.edit
import ru.tensor.sbis.localfeaturetoggle.data.FeatureSet
import ru.tensor.sbis.localfeaturetoggle.data.Feature

private const val LOCAL_FEATURE_TOGGLE_CODE_PREFS = "LOCAL_FEATURE_TOGGLE_CODE_PREFS"

/**
 * Фичетогл сервис.
 *
 * @author mb.kruglova
 */
class LocalFeatureToggleService(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(LOCAL_FEATURE_TOGGLE_CODE_PREFS, Context.MODE_PRIVATE)

    /**
     * Список всех фич.
     */
    val allFeatures: List<Feature> = FeatureSet.values().map { Feature(it.id, it.description) }
        .onEach {
            it.isActivated = sharedPreferences.contains(it.name)
        }

    /**
     * Проверка активности фичи.
     */
    fun isFeatureActivated(feature: Feature): Boolean {
        return sharedPreferences.contains(feature.name)
    }

    /**
     * Проверка активности фичи.
     */
    fun isFeatureActivated(feature: FeatureSet): Boolean {
        return sharedPreferences.contains(feature.id)
    }

    /**
     *  Обновление активности фичи.
     */
    internal fun updateFeature(feature: Feature, isActivated: Boolean) {
        sharedPreferences.edit {
            if (isActivated) {
                putBoolean(feature.name, true)
            } else {
                remove(feature.name)
            }
        }
    }
}