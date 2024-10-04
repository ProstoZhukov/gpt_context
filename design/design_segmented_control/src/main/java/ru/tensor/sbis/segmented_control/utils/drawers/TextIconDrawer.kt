package ru.tensor.sbis.segmented_control.utils.drawers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.TextPaint
import androidx.annotation.Dimension
import androidx.annotation.Px
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.utils.StaticLayoutConfigurator
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Класс для рисования иконок в сегмент-контроле.
 *
 * @author ps.smirnyh
 */
internal class TextIconDrawer(
    private val icon: Char,
    @Px private val iconSize: Int,
    @Px segmentSize: Int,
    context: Context
) : ControlComponentDrawer {

    /**
     * Сторона вписанного квадрата, где легально размещать иконки и тексты
     */
    @Dimension
    private val squareSize = sqrt(2F) * segmentSize / 2F
    private val iconTypeface: Typeface = TypefaceManager.getSbisMobileIconTypeface(context)

    private val activeLayout: Layout = run {
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = iconSize.toFloat()
            typeface = iconTypeface
            this.color = color
        }

        StaticLayoutConfigurator.createStaticLayout(icon.toString(), textPaint) {
            includeFontPad = false
            alignment = Layout.Alignment.ALIGN_CENTER
            maxLines = Int.MAX_VALUE
            width = if (squareSize == 0F) -1 else squareSize.roundToInt()
        }
    }

    override var isVisible = true

    override val width: Float
        get() = activeLayout.width.toFloat()

    override val height: Float
        get() = activeLayout.height.toFloat()

    override fun setTint(color: Int) = activeLayout.run {
        if (paint.color == color) {
            false
        } else {
            paint.color = color
            true
        }
    }

    override fun draw(canvas: Canvas) {
        if (isVisible) {
            activeLayout.draw(canvas)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TextIconDrawer

        if (icon != other.icon) return false
        if (iconSize != other.iconSize) return false

        return true
    }

    override fun hashCode(): Int {
        var result = icon.hashCode()
        result = 31 * result + iconSize
        return result
    }
}