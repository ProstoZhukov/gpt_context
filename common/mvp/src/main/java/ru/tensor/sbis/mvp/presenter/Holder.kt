package ru.tensor.sbis.mvp.presenter

import androidx.lifecycle.ViewModel

/**
 * Вспомогательный класс для работы по сохранению презентера
 * @param INSTANCE тип презентера
 * @property instance презентер
 * @see [BasePresenterFragment]
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
class Holder<INSTANCE>(var instance: INSTANCE?, private val doOnCleared: (INSTANCE) -> Unit = {}) : ViewModel() {

    override fun onCleared() {
        doOnCleared.invoke(instance!!)
        instance = null
    }
}