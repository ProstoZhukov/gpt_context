package ru.tensor.sbis.plugin_struct.feature


/**
 * Поставщик публичного API [Feature]. Позволяет добиться ленивости при инициализации плагинов.
 *
 * @author kv.martyshenko
 */
fun interface FeatureProvider<F : Feature> {
    fun get(): F
}
