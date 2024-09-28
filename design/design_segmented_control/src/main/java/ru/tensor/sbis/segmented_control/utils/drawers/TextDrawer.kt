package ru.tensor.sbis.segmented_control.utils.drawers

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.TextPaint
import androidx.annotation.Px
import ru.tensor.sbis.design.custom_view_tools.utils.StaticLayoutConfigurator
import ru.tensor.sbis.design.custom_view_tools.utils.getTextWidth
import kotlin.math.roundToInt

/**
 * Класс для рисования текста в сегмент-контроле.
 *
 * @author ps.smirnyh
 */
internal class TextDrawer(
    private val text: CharSequence,
    @Px private val textHeight: Int
) : ControlTextComponentDrawer {

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = textHeight.toFloat()
    }

    private val textWidth = textPaint.getTextWidth(text)

    private var layout: Layout = createLayout()

    override var isVisible = true

    override val width: Float
        get() = layout.width.toFloat()

    override var maxWidth: Float = Float.MAX_VALUE
        set(value) {
            if (field != value) {
                field = value
                layout = createLayout()
            }
        }

    override val height: Float
        get() = layout.height.toFloat()

    override fun setTint(color: Int) = layout.run {
        if (paint.color == color) {
            false
        } else {
            paint.color = color
            true
        }
    }

    override fun draw(canvas: Canvas) {
        if (isVisible) {
            layout.draw(canvas)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TextDrawer

        if (text != other.text) return false
        if (textHeight != other.textHeight) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + textHeight
        return result
    }

    private fun createLayout() = StaticLayoutConfigurator.createStaticLayout(text, textPaint) {
        includeFontPad = false
        alignment = Layout.Alignment.ALIGN_CENTER
        width = minOf(textWidth, maxWidth.roundToInt())
    }
}