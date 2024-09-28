package ru.tensor.sbis.application_tools.base

import androidx.lifecycle.ViewModel

/**
 * @author du.bykov
 *
 * Вспомогательный класс для работы по сохранению презентера см. [BasePresenterFragment]
 */
class Holder<INSTANCE>(var instance: INSTANCE?, private val doOnCleared: (INSTANCE) -> Unit = {}) : ViewModel() {

    override fun onCleared() {
        doOnCleared.invoke(instance!!)
        instance = null
    }
}