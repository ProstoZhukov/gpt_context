package ru.tensor.sbis.design.retail_views.common.span

import android.text.TextPaint
import android.text.style.MetricAffectingSpan

/**
 * Прижимает текст к нижнему краю
 */
class BottomAlignSpan : MetricAffectingSpan() {

    override fun updateMeasureState(textPaint: TextPaint) {
        update(textPaint)
    }

    override fun updateDrawState(textPaint: TextPaint?) {
        update(textPaint)
    }

    private fun update(textPaint: TextPaint?) {
        textPaint?.let {
            textPaint.baselineShift += textPaint.descent().toInt()
        }
    }
}