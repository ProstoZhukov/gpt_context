package ru.tensor.sbis.common.util.sharedprefs

/**
 * Провайдер недавних значений.
 *
 * @author am.boldinov
 */
interface RecentProvider<T> {

    /**
     * Добавить новое значение.
     */
    fun push(value: T)

    /**
     * Проверить, присутствует ли указанное значение в недавних.
     */
    fun contains(value: T): Boolean

    /**
     * Очистить недавние значения.
     */
    fun clear()

}