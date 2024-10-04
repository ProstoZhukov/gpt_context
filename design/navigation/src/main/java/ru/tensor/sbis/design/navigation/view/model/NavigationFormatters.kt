/**
 * Форматтеры для числовых значений.
 *
 * @author ma.kolpakov
 * Создан 10/9/2019
 */

@file:JvmName("NavigationFormat")

package ru.tensor.sbis.design.navigation.view.model

import androidx.arch.core.util.Function
import ru.tensor.sbis.design.utils.formatCount
import ru.tensor.sbis.design.utils.formatCountSimple

/**
 * @see formatCount
 */
@JvmField
val DEFAULT_FORMAT = Function<Int, String?> { count ->
    formatCount(count)
}

/**
 * @see formatCountSimple
 */
@Suppress("unused")
@JvmField
val SIMPLE_FORMAT = Function<Int, String?> { count ->
    formatCountSimple(count)
}