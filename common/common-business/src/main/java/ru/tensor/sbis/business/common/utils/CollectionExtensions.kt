@file:Suppress("NOTHING_TO_INLINE")

package ru.tensor.sbis.business.common.utils

/**
 * Содержит ли коллекция все элементы из vararg.
 * @see Collection.containsAll
 */
inline fun <T> Collection<T>.containsAll(vararg elements: T): Boolean =
    elements.all { contains(it) }

/**
 * Содержит ли коллекция хотя бы один элемент из списка
 */
inline fun <T> Collection<T>.containsAny(elements: Collection<T>): Boolean =
    elements.any { contains(it) }

/**
 * Содержит ли коллекция хотя бы один элемент из vararg
 */
inline fun <T> Collection<T>.containsAny(vararg elements: T): Boolean =
    elements.any { contains(it) }
