@file:JvmName("ArrayListExt")

package ru.tensor.sbis.common.util

import kotlin.collections.map as originalMap
import kotlin.collections.mapIndexed as originalMapIndexed
import kotlin.collections.mapNotNull as originalMapNotNull

/**
 * @author sa.nikitin
 */

/**
 * Функция-расширение ArrayList
 * Как [originalMap] только от ArrayList и возвращает ArrayList
 */
inline fun <T, R> ArrayList<T>.map(transform: (T) -> R): ArrayList<R> {
    return mapTo(ArrayList(size), transform)
}

/**
 * Функция-расширение ArrayList
 * Как [originalMapIndexed] только от ArrayList и возвращает ArrayList
 */
inline fun <T, R> ArrayList<T>.mapIndexed(transform: (Int, T) -> R): ArrayList<R> {
    return mapIndexedTo(ArrayList(size), transform)
}

/**
 * Функция-расширение ArrayList
 * Как [originalMapNotNull] только от ArrayList и возвращает ArrayList
 */
inline fun <T, R : Any> ArrayList<T>.mapNotNull(transform: (T) -> R?): ArrayList<R> {
    return mapNotNullTo(ArrayList(size), transform)
}

/**
 * Функция полезна для передачи списков в контроллер, так как там часто требуется именно ArrayList.
 * Уже используется в некоторых модулях
 */
fun <T> Collection<T>.asArrayList() = this as? ArrayList<T> ?: ArrayList(this)