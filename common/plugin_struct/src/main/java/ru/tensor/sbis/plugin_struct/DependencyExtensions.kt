package ru.tensor.sbis.plugin_struct

import ru.tensor.sbis.plugin_struct.Dependency.Builder
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider

/**
 * Метод для подключения зависимостей, наличие которых определяется фиче-флагом.
 *
 * Важно: перед использованием данной функции убедитесь, что переменная
 *        val dependency: Dependency объявлена как 'lazy', в противном
 *        случае корректная работа не гарантирована.
 * */
fun <F : Feature> Builder.requireIf(featureEnabled: Boolean, type: Class<F>, inject: (FeatureProvider<F>) -> Unit): Builder {
    return if(featureEnabled) require(type, inject) else this
}

/**
 * Метод с инвертированной логикой для подключения зависимостей, наличие которых определяется фиче-флагом.
 *
 * Важно: перед использованием данной функции убедитесь, что переменная
 *        val dependency: Dependency объявлена как 'lazy', в противном
 *        случае корректная работа не гарантирована.
 * */
fun <F : Feature> Builder.requireNotIf(featureEnabled: Boolean, type: Class<F>, inject: (FeatureProvider<F>) -> Unit): Builder {
    return if(!featureEnabled) require(type, inject) else this
}