package ru.tensor.sbis.mvvm.di

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

/**
 * Ключ поставщика реализации [ViewModel] с внедрением зависимостей.
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
@Target(allowedTargets = [AnnotationTarget.FUNCTION])
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)