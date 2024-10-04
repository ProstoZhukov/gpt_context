package ru.tensor.sbis.design.selection.ui.utils.vm.choose_all

import androidx.lifecycle.LiveData
import io.reactivex.Observable

/**
 * Интерфейс вьюмодели для управления фиксированной кнопкой ("Выбрать все", "Новая группа")
 *
 * @author ma.kolpakov
 */
internal interface FixedButtonViewModel<DATA> {

    /**
     * Пользовательские данные для отображения в кнопке
     */
    val fixedButtonData: LiveData<DATA>

    /**
     * Подписка на отображение кнопки
     */
    val showFixedButton: LiveData<Int>

    /**
     * Подписка на нажатие кнопки
     *
     * @see onFixedButtonClicked
     */
    val fixedButtonClicked: Observable<DATA>

    /**
     * Установка состояния видимости заглушки
     */
    fun setStubVisible(visibility: Int)

    /**
     * Метод для публикации нажатия на кнопку
     *
     * @see fixedButtonClicked
     */
    fun onFixedButtonClicked()
}