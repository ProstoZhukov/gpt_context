package ru.tensor.sbis.common.util

import android.content.Context
import kotlin.math.abs
import kotlin.math.ceil

class ChangeableSpeedSmoothScroller(
    context: Context,
    millisecondsPerInch: Float
) : androidx.recyclerview.widget.LinearSmoothScroller(context) {

    private val millisecondsPerPx = millisecondsPerInch / context.resources.displayMetrics.densityDpi.toFloat()

    override fun calculateTimeForScrolling(dx: Int): Int =
        ceil(abs(dx.toDouble()) * millisecondsPerPx).toInt()
}