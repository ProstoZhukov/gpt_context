package ru.tensor.sbis.list.base.presentation

import androidx.lifecycle.MutableLiveData
import ru.tensor.sbis.list.view.SbisList
import ru.tensor.sbis.list.view.utils.ListData
import ru.tensor.sbis.list.view.utils.Plain

/**
 * Данные списка для использования в [SbisList].
 */
class ListLiveData : MutableLiveData<ListData>(Plain()) {

    /**
     * Установить пустое значение.
     */
    fun setEmpty() {
        value = Plain()
    }

    /**
     * Установить пустое значение отложенной задачкй в UI потоке см [MutableLiveData.postValue].
     */
    fun postEmpty() {
        postValue(Plain())
    }
}