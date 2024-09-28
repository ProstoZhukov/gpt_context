package ru.tensor.sbis.mvp.presenter

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Базовый делегат для ViewModel
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
abstract class BaseArchDelegate<VIEW_MODEL>(
    private var creatingMethod: (() -> VIEW_MODEL)?,
    private val key: String? = null,
    private var doOnCleared: ((VIEW_MODEL) -> Unit) = {}
) : ReadOnlyProperty<LifecycleOwner, VIEW_MODEL> {
    private var value: VIEW_MODEL? = null

    override operator fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): VIEW_MODEL {
        if (value == null) {
            value = getViewModel()
        }

        return value!!
    }

    private fun getViewModel(): VIEW_MODEL {
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val instance = creatingMethod!!.invoke()
                creatingMethod = null
                @Suppress("UNCHECKED_CAST")
                return (Holder(instance, doOnCleared) as T)
            }
        }
        val holder = getHolder(getViewModelProvider(factory), key)
        @Suppress("UNCHECKED_CAST")
        return holder.instance as VIEW_MODEL
    }

    protected abstract fun getViewModelProvider(factory: ViewModelProvider.Factory): ViewModelProvider

    private fun getHolder(provider: ViewModelProvider, key: String?) =
        if (key != null) provider.get(key, Holder::class.java) else provider.get(
            this::class.java.canonicalName!!,
            Holder::class.java
        )
}