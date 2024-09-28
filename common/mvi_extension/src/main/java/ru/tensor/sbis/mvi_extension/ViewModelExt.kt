package ru.tensor.sbis.mvi_extension

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

/**
 * Created by Aleksey Boldinov on 31.08.2022.
 */

inline fun <reified T> ViewModelStoreOwner.wrapViewModel(
    key: String,
    crossinline factory: () -> T
): T {
    return viewModelStore.wrapViewModel(key, factory)
}

inline fun <reified T> ViewModelStore.wrapViewModel(
    key: String,
    crossinline factory: () -> T
): T {
    @Suppress("UNCHECKED_CAST")
    return ViewModelProvider(
        this,
        object : ViewModelProvider.Factory {
            override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
                return ObjectViewModel(factory.invoke()) as VM
            }
        })[key + "_" + T::class.java.canonicalName!!, ObjectViewModel::class.java].obj as T
}

class ObjectViewModel<T>(val obj: T) : ViewModel()