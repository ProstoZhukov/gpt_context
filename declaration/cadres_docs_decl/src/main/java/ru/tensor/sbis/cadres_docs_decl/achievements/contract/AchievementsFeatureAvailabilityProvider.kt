package ru.tensor.sbis.cadres_docs_decl.achievements.contract

import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Провайдер проверки, доступна ли фича ПИВ
 */
interface AchievementsFeatureAvailabilityProvider : Feature {
    /**
     * Проверка доступности функционала создания ПИВ
     */
    fun createFeatureAvailable(recipientUUID: UUID): Flow<Boolean>
}