package ru.tensor.sbis.viper.arch.provider

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Предоставляет идентификатор контейнера
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
interface ContainerFrameIdProvider : Feature {

    /** @SelfDocumented */
    val containerFrameId: Int
}