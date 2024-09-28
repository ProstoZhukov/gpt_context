package ru.tensor.sbis.feature_ctrl

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс провайдера [SbisFeatureService]
 *
 *  @author mb.kruglova
 */
interface SbisFeatureServiceProvider : Feature {
    /**
     * Удаленный фичетогл сервис
     */
    val sbisFeatureService: SbisFeatureService
}
