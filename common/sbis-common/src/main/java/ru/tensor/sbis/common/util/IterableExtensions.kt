@file:Suppress("NOTHING_TO_INLINE")

package ru.tensor.sbis.common.util

/**
 * Являются ли все элементы [Iterable] равными по equals
 *
 * @param ifEmpty будет возвращено, если элементов нет
 *
 * @return равны ли элементы
 */
inline fun <T> Iterable<T>.allItemsAreEquals(ifEmpty: Boolean = false): Boolean {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return ifEmpty

    val first = iterator.next()
    if (!iterator.hasNext()) return true

    while (iterator.hasNext()) {
        if (first != iterator.next()) return false
    }
    return true
}

/**
 * Являются ли все элементы [Iterable] равными по equals заданного свойства или признака.
 * Например, можно проверить, что длина всех строк в List<String> равна:
 * listOf("cat", "dog").allItemsAreEqualsBy{ it.length }
 *
 * @param ifEmpty будет возвращено, если элементов нет
 *
 * @return равны ли элементы
 */
inline fun <T, R> Iterable<T>.allItemsAreEqualsBy(ifEmpty: Boolean = false, selector: (T) -> R): Boolean {
    val iterator = this.iterator()

    if (!iterator.hasNext()) return ifEmpty

    val first = selector(iterator.next())
    if (!iterator.hasNext()) return true

    while (iterator.hasNext()) {
        if (first != selector(iterator.next())) return false
    }
    return true
}
