@file:Suppress("NOTHING_TO_INLINE")

package ru.tensor.sbis.business.common.utils

/**
 * Содержит ли [CharSequence] хотя бы одну из vararg подстрок
 */
inline fun CharSequence.containsAny(
    vararg substrings: CharSequence,
    ignoreCase: Boolean = false
): Boolean = substrings.any { contains(it, ignoreCase) }

/**
 * Содержит ли [CharSequence] хотя бы одну подстрок из vararg
 */
inline fun CharSequence.containsAny(
    substrings: Collection<CharSequence>,
    ignoreCase: Boolean = false
): Boolean = substrings.any { contains(it, ignoreCase) }

/**
 * Содержит ли [CharSequence] все подстроки из vararg
 */
inline fun CharSequence.containsAll(
    vararg substrings: CharSequence,
    ignoreCase: Boolean = false
): Boolean = substrings.all { contains(it, ignoreCase) }


/**
 * Содержит ли [CharSequence] все подстроки из списка
 */
inline fun CharSequence.containsAll(
    substrings: Collection<CharSequence>,
    ignoreCase: Boolean = false
): Boolean = substrings.all { contains(it, ignoreCase) }
