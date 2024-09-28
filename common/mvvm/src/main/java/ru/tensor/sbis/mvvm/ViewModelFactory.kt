package ru.tensor.sbis.mvvm

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

/**
 * Служебный класс представляющий реализацию фабрики отвечающей за создание джереник экземпляров вью-моделей
 * @param VIEW_MODEL тип предоставляемой вью-модели
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
class ViewModelFactory<VIEW_MODEL : ViewModel> @Inject constructor(
    private val viewModel: Provider<VIEW_MODEL>
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <VIEW_MODEL : ViewModel> create(modelClass: Class<VIEW_MODEL>): VIEW_MODEL {
        return viewModel.get() as VIEW_MODEL
    }
}

@Deprecated("Устаревший подход, переходим на mvi_extension")
inline fun <reified VIEW_MODEL : ViewModel> Fragment.withFactory(
    factory: ViewModelFactory<VIEW_MODEL>
): VIEW_MODEL =
    ViewModelProvider(
        this,
        factory
    )[VIEW_MODEL::class.java]

@Deprecated("Устаревший подход, переходим на mvi_extension")
inline fun <reified VIEW_MODEL : ViewModel> FragmentActivity.withFactory(
    factory: ViewModelFactory<VIEW_MODEL>
): VIEW_MODEL =
    ViewModelProvider(
        this,
        factory
    )[VIEW_MODEL::class.java]

@Deprecated("Устаревший подход, переходим на mvi_extension")
fun <VIEW_MODEL : ViewModel> FragmentActivity.withFactory(
    factory: ViewModelFactory<VIEW_MODEL>,
    vmClass: Class<VIEW_MODEL>
): VIEW_MODEL =
    ViewModelProvider(
        this,
        factory
    )[vmClass]

@Deprecated("Устаревший подход, переходим на mvi_extension")
fun <VIEW_MODEL : ViewModel> Fragment.withFactory(
    factory: ViewModelFactory<VIEW_MODEL>,
    vmClass: Class<VIEW_MODEL>
): VIEW_MODEL =
    ViewModelProvider(
        this,
        factory
    )[vmClass]



