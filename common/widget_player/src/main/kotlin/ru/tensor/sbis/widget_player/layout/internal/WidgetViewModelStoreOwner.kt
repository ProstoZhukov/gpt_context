package ru.tensor.sbis.widget_player.layout.internal

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner

/**
 * @author am.boldinov
 */
internal class WidgetViewModelStoreOwner : ViewModelStoreOwner {

    private val fallbackStore = lazy(LazyThreadSafetyMode.NONE) {
        ViewModelStore()
    }
    private var rootViewModelStore: ViewModelStore? = null

    override val viewModelStore: ViewModelStore
        get() = rootViewModelStore ?: fallbackStore.value

    fun dispatchAttached(view: View) {
        view.findViewTreeViewModelStoreOwner()?.viewModelStore?.let {
            if (fallbackStore.isInitialized()) {
                fallbackStore.value.copyTo(it)
            }
            rootViewModelStore = it
        }
    }

    @SuppressLint("RestrictedApi")
    private fun ViewModelStore.copyTo(other: ViewModelStore) {
        keys().forEach { key ->
            get(key)?.let {
                other.put(key, it)
            }
        }
    }
}