package ru.tensor.sbis.design.list_header

import java.util.Date

/**
 * Возвращает модель даты элемента в определённой позиции
 * Должен быть реализован адаптером
 *
 * К примеру:
 * ```
 * override fun getItemDateTime(position: Int): LocalDateTime = content[position].date
 * ```
 *
 * @author ra.petrov
 */
interface DateTimeAdapter {

    /**
     * Возвращает модель даты элемента в определённой позиции
     */
    fun getItemDateTime(position: Int): Date?
}