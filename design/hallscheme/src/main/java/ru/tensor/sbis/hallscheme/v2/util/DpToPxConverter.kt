package ru.tensor.sbis.hallscheme.v2.util

import android.content.Context
import android.util.DisplayMetrics

/**
 * Конвертер из dp в px.
 * @author aa.gulevskiy
 */
internal class DpToPxConverter(context: Context) {

    private val displayMetrics = context.getActivityContext().resources.displayMetrics
    private val densityDpi: Float = displayMetrics.densityDpi.toFloat()

    /**@SelfDocumented*/
    val factor = (densityDpi / DisplayMetrics.DENSITY_DEFAULT.toFloat())

    /**@SelfDocumented*/
    fun fromDpToPixels(dp: Float): Int {
        return (dp * factor).toInt()
    }

    /**@SelfDocumented*/
    fun fromDpToPixels(dp: Double): Double {
        return (dp * factor)
    }

    /**@SelfDocumented*/
    fun intToDp(value: Int): Float =
        value * displayMetrics.density + 0.5f
}