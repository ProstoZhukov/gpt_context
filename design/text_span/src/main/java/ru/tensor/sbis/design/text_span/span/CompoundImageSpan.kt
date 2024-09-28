package ru.tensor.sbis.design.text_span.span

import android.graphics.Paint
import android.graphics.drawable.Drawable
import com.facebook.widget.text.span.BetterImageSpan

/**
 * Расширенная реализация [android.text.style.ImageSpan]
 * с возможностью выравнивания и динамического изменения размера изображения в зависимости
 * от размера текста
 *
 * @author am.boldinov
 */
class CompoundImageSpan @JvmOverloads constructor(
    drawable: Drawable,
    verticalAlignment: Int = ALIGN_CENTER
) : BetterImageSpan(drawable, verticalAlignment) {

    private val emptyInitialBounds = drawable.bounds.isEmpty

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fontMetrics: Paint.FontMetricsInt?
    ): Int {
        if (emptyInitialBounds) {
            val imageSize = paint.textSize.toInt()
            drawable.bounds.set(0, 0, imageSize, imageSize)
        }
        return super.getSize(paint, text, start, end, fontMetrics)
    }
}