package ru.tensor.sbis.plugin_struct.feature

/**
 * Контэйнер содержащий ссылки на класс типа [Feature] и поставщик [FeatureProvider]
 *
 * @author kv.martyshenko
 */
data class FeatureWrapper<F : Feature>(
    val type: Class<F>,
    val provider: FeatureProvider<out F>
)