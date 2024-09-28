/**
 * @author ma.kolpakov
 */
package ru.tensor.sbis.review

/**
 * Функция переводит любое перечисление в строку
 */
internal fun enumToString(enum: Enum<*>): String {
    return "${enum::class.java.name}|${enum.name}"
}