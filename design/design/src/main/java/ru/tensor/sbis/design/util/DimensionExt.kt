/**
 * Утилитные функции для конвертации размеров.
 *
 * @author da.zolotarev
 */

package ru.tensor.sbis.design.util

import android.content.Context
import androidx.annotation.Dimension
import androidx.annotation.Dimension.DP
import kotlin.math.roundToInt

/**
 * Конвертировать dp в px.
 */
fun Context.dpToPx(@Dimension(unit = DP) dp: Int): Int =
    (dp * resources.displayMetrics.density).roundToInt()

/**
 * Конвертировать dp в px без округления.
 */
fun Context.dpToDimension(@Dimension(unit = DP) dp: Int) = dp * resources.displayMetrics.density

/**
 * Конвертировать px в dp.
 */
fun Context.pxToDp(px: Int): Int =
    (px / resources.displayMetrics.density).roundToInt()