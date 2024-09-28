package ru.tensor.sbis.viper.helper

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author ga.malinskiy
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
fun <T> MutableLiveData<T>.notifyObserver() {
    value = value
}

@Deprecated("Устаревший подход, переходим на mvi_extension")
@Suppress("unused")
fun ViewModel.notifyObserver() {
    this::class.members.forEach {
        (it as? MutableLiveData<*>)?.notifyObserver()
    }
}