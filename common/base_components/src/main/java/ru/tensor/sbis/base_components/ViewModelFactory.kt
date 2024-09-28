@file:Suppress("DEPRECATION", "unused")

package ru.tensor.sbis.base_components

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import javax.inject.Inject
import javax.inject.Provider

@SuppressWarnings("ALL")
@Deprecated("Использовать реализацию фабрики из модуля mvvm")
class ViewModelFactory<VIEW_MODEL : ViewModel> @Inject constructor(private val viewModel: Provider<VIEW_MODEL>) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <VIEW_MODEL : ViewModel> create(modelClass: Class<VIEW_MODEL>): VIEW_MODEL {
        return viewModel.get() as VIEW_MODEL
    }
}

inline fun <reified VIEW_MODEL : ViewModel> Fragment.withFactory(factory: ViewModelFactory<VIEW_MODEL>): VIEW_MODEL =
    ViewModelProviders.of(
            this,
            factory
    )[VIEW_MODEL::class.java]

inline fun <reified VIEW_MODEL : ViewModel> FragmentActivity.withFactory(factory: ViewModelFactory<VIEW_MODEL>): VIEW_MODEL =
    ViewModelProviders.of(
            this,
            factory
    )[VIEW_MODEL::class.java]

fun <VIEW_MODEL : ViewModel> FragmentActivity.withFactory(
    factory: ViewModelFactory<VIEW_MODEL>,
    vmClass: Class<VIEW_MODEL>
): VIEW_MODEL =
    ViewModelProviders.of(
            this,
            factory
    )[vmClass]

fun <VIEW_MODEL : ViewModel> Fragment.withFactory(
    factory: ViewModelFactory<VIEW_MODEL>,
    vmClass: Class<VIEW_MODEL>
): VIEW_MODEL =
    ViewModelProviders.of(
            this,
            factory
    )[vmClass]



