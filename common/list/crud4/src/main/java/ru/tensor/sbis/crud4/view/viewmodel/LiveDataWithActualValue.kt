package ru.tensor.sbis.crud4.view.viewmodel

import androidx.lifecycle.MutableLiveData

/**
 * MutableLiveData выполняет установку значения методом postValue отложено. Этот же класс дает возможность получить
 * актуальное значения сразу после вызова метода postValue.
 */
class LiveDataWithActualValue<T>(value: T) : MutableLiveData<T>() {

    /**
     * Актуальное значения, последнее, которое было передано в postValue или setValue.
     */
    var actualValue: T = value
        private set

    override fun postValue(value: T) {
        actualValue = value
        super.postValue(value)
    }

    override fun setValue(value: T) {
        actualValue = value
        super.setValue(value)
    }
}