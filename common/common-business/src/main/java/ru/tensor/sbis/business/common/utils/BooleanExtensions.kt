@file:Suppress("NOTHING_TO_INLINE")

package ru.tensor.sbis.business.common.utils

/**
 * Если объект [Boolean] равен null, возвращает true, иначе возвращает сам объект
 */
inline fun Boolean?.orTrue() = this ?: true

/**
 * Если объект [Boolean] равен null, возвращает false, иначе возвращает сам объект
 */
inline fun Boolean?.orFalse() = this ?: false
