package ru.tensor.sbis.business.common.data

import androidx.databinding.BaseObservable

/**
 * Поставщик вьюмодели [BaseObservable] из модели данных
 *
 * @author as.chadov
 */
interface ViewModelProvider {

    /**
     * Создаёт вьюмодель, представляющую данный объект
     *
     * @return вьюмодель объекта
     */
    fun toBaseObservableVM(): BaseObservable
}