package ru.tensor.sbis.widget_player.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

/**
 * @author am.boldinov
 */

internal inline fun <reified VM : ViewModel> ViewModelStoreOwner.viewModelLazy(
    crossinline factory: () -> VM
): Lazy<VM> {
    return ViewModelLazy(
        viewModelClass = VM::class,
        storeProducer = {
            viewModelStore
        },
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return factory.invoke() as T
                }
            }
        })
}