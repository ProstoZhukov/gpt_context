package ru.tensor.sbis.design.navigation.view.model

import androidx.arch.core.util.Function
import io.reactivex.Observable

/**
 * Модель счётчика для элемента меню.
 *
 * @author ma.kolpakov
 * Создан 11/8/2018
 */
interface NavigationCounter {

    /**
     * Подписка на изменение количества новых объектов в разделе меню.
     */
    val newCounter: Observable<Int>

    /**
     * Подписка на изменение общего количества объектов в разделе меню.
     */
    val totalCounter: Observable<Int>

    /**
     * Метод для получения функции, определяющей форматирование счётчиков на UI.
     *
     * @param type тип форматирования (зависящий от расположения счётчика)
     */
    @Suppress("unused", "SameReturnValue")
    fun getFormatter(type: FormatterType): Function<Int, String?> = DEFAULT_FORMAT

    /**
     * Показывать ли счётчик [totalCounter] если счётчик [newCounter] пуст. Например, для отображения прочитанных или
     * просмотренных. Используется в ННП.
     */
    @Suppress("SameReturnValue")
    fun useTotalCounterAsSecondary(): Boolean = false

    /**
     * Использовать счётчик из определенного контроллера, а не из общего. Используется в ННП.
     */
    @Suppress("SameReturnValue")
    fun useCounterFromController(): Boolean = false
}

/**
 * Тип форматирования счётчика.
 */
@Suppress("unused")
enum class FormatterType {
    NAV_VIEW_NEW,
    NAV_VIEW_TOTAL
}