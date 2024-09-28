package ru.tensor.sbis.mvvm.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import javax.inject.Inject
import javax.inject.Provider

/**
 * Файбрика для создания [ViewModel] с внедрением зависимостей.
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
class ViewModelFactory @Inject constructor(
    private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return viewModels[modelClass]?.get() as? T
            ?: throw IllegalStateException("No module with @Binds method for $modelClass")
    }
}

@Deprecated("Устаревший подход, переходим на mvi_extension")
inline fun <reified VIEW_MODEL : ViewModel> Fragment.withFactory(
    factory: ViewModelProvider.Factory
): VIEW_MODEL =
    ViewModelProvider(
        this,
        factory
    )[VIEW_MODEL::class.java]

@Deprecated("Устаревший подход, переходим на mvi_extension")
inline fun <reified VIEW_MODEL : ViewModel> ViewModelStore.withFactory(
    factory: ViewModelProvider.Factory
): VIEW_MODEL =
    ViewModelProvider(
        this,
        factory
    )[VIEW_MODEL::class.java]