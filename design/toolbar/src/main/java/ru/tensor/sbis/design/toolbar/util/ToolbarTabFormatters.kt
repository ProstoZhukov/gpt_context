@file:JvmName("ToolbarTabFormat")

/**
 * @author us.bessonov
 */
package ru.tensor.sbis.design.toolbar.util

import android.os.Parcelable
import androidx.arch.core.util.Function
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.utils.formatCount
import ru.tensor.sbis.design.utils.formatCountSimple

/** @SelfDocumented */
interface TabFormatter : (Int) -> String, Parcelable

/**
 * Форматтер по умолчанию
 * @see formatCount
 */
@Parcelize
class DefaultFormatter : TabFormatter {
    override fun invoke(count: Int): String {
        return formatCount(count)
    }
}

/**
 * @see formatCount
 */
@JvmField
val DEFAULT_TAB_FORMAT = DefaultFormatter()

/**
 * @see formatCountSimple
 */
@JvmField
@Suppress("unused")
val SIMPLE_TAB_FORMAT = Function<Int, String> { count ->
    formatCountSimple(count)
}