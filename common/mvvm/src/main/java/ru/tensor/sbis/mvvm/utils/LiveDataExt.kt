package ru.tensor.sbis.mvvm.utils

import androidx.lifecycle.LiveData

/**
 * Получение текущего значения в LiveData
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
@Suppress("UNCHECKED_CAST")
val <T> LiveData<T>.get
    get() = this.value as T