package ru.tensor.sbis.manage_features_test_main_screen_addon

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.tensor.sbis.feature_ctrl.SbisFeatureService

/**
 * Поставщик статуса доступности раздела "Проверка функционала" в приложении
 *
 * @author us.bessonov
 */
internal object FeatureTestAvailabilityChecker {
    private val featureTestScope: CoroutineScope = CoroutineScope(Job())

    /** @SelfDocumented */
    fun getAndUpdateAvailability(
        manageFeaturesFeature: SbisFeatureService
    ): LiveData<Boolean> {
        return update(manageFeaturesFeature)
    }

    private fun update(manageFeaturesFeature: SbisFeatureService) =
        manageFeaturesFeature.getFeatureInfoFlow(listOf(FEATURES_IDENTIFIER))
            .map { it.state == true }
            .stateIn(featureTestScope, SharingStarted.Lazily, manageFeaturesFeature.isActive(FEATURES_IDENTIFIER))
            .asLiveData()
}

private const val FEATURES_IDENTIFIER = "test3xfeat"