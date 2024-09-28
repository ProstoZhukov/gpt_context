package ru.tensor.sbis.richtext.util

import android.graphics.Typeface
import android.text.Layout.Alignment
import android.text.StaticLayout
import android.text.TextPaint
import androidx.annotation.ColorInt

/**
 * Класс для работы со StaticLayout, добавляющий кеширование значений и быстрый доступ к ним.
 *
 * @author am.boldinov
 */
class StaticLayoutDelegate @JvmOverloads constructor(
    private val paint: TextPaint,
    private val alignment: Alignment = Alignment.ALIGN_NORMAL,
    private val ellipsize: Boolean = true
) {

    private var layout: StaticLayout? = null
    private var text: String? = null
    private var width = 0
    private var measuredText: String? = null
    private var measuredWidth = 0f
    private var renderedWidth = 0f

    /**
     * Возвращает ранее построенный с помощью [measure] экземпляр [StaticLayout].
     */
    fun get(): StaticLayout? {
        return layout
    }

    /**
     * Устанавливает шрифт текста.
     */
    fun setTypeface(typeface: Typeface?) {
        if (paint.typeface != typeface) {
            paint.typeface = typeface
            invalidate()
        }
    }

    /**
     * Устанавливает размер текста.
     */
    fun setTextSize(textSize: Float) {
        if (paint.textSize != textSize) {
            paint.textSize = textSize
            invalidate()
        }
    }

    /**
     * Устанавливает цвет текста.
     */
    fun setColor(@ColorInt color: Int) {
        if (paint.color != color) {
            paint.color = color
            invalidate()
        }
    }

    /**
     * Строит макет [StaticLayout] текста для отрисовки на канвасе на основе его параметров в [TextPaint].
     * Кеширует значение по переданной строке [text] и максимальному размеру [width].
     */
    fun measure(text: String, width: Int): StaticLayout {
        return layout?.let {
            if (this.text == text && this.width == width) {
                it
            } else null
        } ?: run {
            if (ellipsize) {
                StaticLayoutProxy.createEllipsize(text, paint, alignment, width)
            } else {
                StaticLayoutProxy.create(text, paint, alignment, width)
            }
        }.also {
            this.text = text
            this.width = width
            layout = it
            renderedWidth = 0f
        }
    }

    /**
     * Измеряет размер текста на основе его параметров в [TextPaint].
     * Кеширует значение по переданной строке [text]
     */
    fun measureText(text: String): Float {
        return if (measuredText == text) {
            measuredWidth
        } else paint.measureText(text).let {
            if (it > 0) {
                it + StaticLayoutProxy.TEXT_ELLIPSIZE_OFFSET
            } else 0f
        }.also {
            measuredText = text
            measuredWidth = it
        }
    }

    /**
     * Возвращает ранее полученное значение ширины текста с помощью [measureText].
     */
    fun getMeasuredWidth(): Float {
        return measuredWidth
    }

    /**
     * Возвращает ширину текста на основе построенного [StaticLayout] с помощью [measure].
     */
    fun getRenderedWidth(): Float {
        return layout?.let {
            renderedWidth.takeIf { w ->
                w > 0f
            } ?: LayoutUtil.getTextWidth(it)
        }?.also { renderedWidth = it } ?: error("You must measure layout before calling this method")
    }

    /**
     * Сбрасывает ранее закешированные значения и измерения.
     */
    fun invalidate() {
        text = null
        measuredText = null
        width = 0
        measuredWidth = 0f
        renderedWidth = 0f
        layout = null
    }

}