package ru.tensor.sbis.design.buttons.base.utils.drawers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.Spannable
import android.text.TextPaint
import android.text.style.ForegroundColorSpan
import androidx.annotation.Dimension
import androidx.annotation.Px
import androidx.core.text.set
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState
import ru.tensor.sbis.design.custom_view_tools.utils.StaticLayoutConfigurator
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * @author ma.kolpakov
 */
internal class TextIconDrawer(
    private val icon: CharSequence,
    @Px private val iconSize: Int,
    @Px buttonSize: Int,
    context: Context
) : ButtonComponentDrawer {

    /**
     * Сторона вписанного квадрата, где легально размещать иконки и тексты.
     */
    @Dimension
    private val squareSize = sqrt(2F) * buttonSize / 2F
    private val iconTypeface: Typeface = TypefaceManager.getSbisMobileIconTypeface(context)
    private val colorSpans: List<ColorSpan>? = (icon as? Spannable)?.run {
        getSpans(0, length, ForegroundColorSpan::class.java)
            .map { span -> ColorSpan(getSpanStart(span), getSpanEnd(span), span) }
    }
    private val activeLayout: Layout = run {
        // Иконки состоят из одного символа. Для них используется специальный шрифт.
        val currentTypeface =
            if (icon.length == 1 && !Character.isLetterOrDigit(icon[0])) {
                iconTypeface
            } else {
                null
            }
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = iconSize.toFloat()
            typeface = currentTypeface
            this.color = color
        }

        StaticLayoutConfigurator.createStaticLayout(icon, textPaint) {
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

    override fun changeState(state: SbisButtonState) {
        colorSpans ?: return
        icon as Spannable
        when (state) {
            SbisButtonState.DISABLED -> {
                colorSpans.forEach {
                    icon.removeSpan(it.span)
                }
            }
            SbisButtonState.ENABLED -> {
                colorSpans.forEach {
                    icon[it.start..it.end] = it.span
                }
            }
            SbisButtonState.IN_PROGRESS -> Unit
        }
    }

    /**
     * Класс для хранения информации о спанах в кнопке.
     */
    private data class ColorSpan(val start: Int, val end: Int, val span: ForegroundColorSpan)
}