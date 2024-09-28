package ru.tensor.sbis.viper.helper

import androidx.lifecycle.MutableLiveData

/**
 * Класс который имеет флаг состояния изменения только когда был изменен после инициализации значений
 *
 * @author ga.malinskiy
 */
class ExtendedLiveData<T> : MutableLiveData<T>() {

    private var previousValue: T? = null

    var changed: Boolean = false

    override fun setValue(value: T?) {
        changed = previousValue != value
        if (changed) {
            previousValue = value
            super.setValue(value)
        }
    }
}