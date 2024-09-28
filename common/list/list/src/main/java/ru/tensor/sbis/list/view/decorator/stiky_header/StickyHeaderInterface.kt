package ru.tensor.sbis.list.view.decorator.stiky_header

import android.view.View
import android.view.ViewGroup

/**
 * Вспомогательный интерфейс для реализации стики-заголовка.
 */
internal interface StickyHeaderInterface {

    /**
     * Найти элемент являющийся заголовком по отношении ячейки с преданной позицией [forPosition] и выполнить метод
     * с найденной ячейкой.
     * @param parent ViewGroup если для ячейка-заголовка виджет еще не создан, то он будет создан и использованием
     * этого объекта.
     * @param func Function2<Int, View, Unit> метод, который будет выполнен для найденной ячейки.
     */
    fun runWithHeaderPosition(forPosition: Int, parent: ViewGroup, func: (Int, View) -> Unit)

    /**
     * Является ли элемент по переданной позиции [position] стики-заголовком.
     */
    fun isSticky(position: Int): Boolean
}