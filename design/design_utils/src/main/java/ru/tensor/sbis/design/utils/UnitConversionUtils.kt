/**
 * Инструменты для конвертации величин
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.design.utils

import android.content.Context
import android.util.DisplayMetrics

/** @SelfDocumented */
fun dpToPx(context: Context, dp: Int): Int {
    val resources = context.resources
    val metrics = resources.displayMetrics
    return (dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}