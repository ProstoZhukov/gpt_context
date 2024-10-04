package ru.tensor.sbis.design.buttons.base.utils.drawers

import android.content.Context
import android.graphics.Canvas
import android.text.Layout
import android.text.Spannable
import android.text.style.AbsoluteSizeSpan
import androidx.annotation.Px
import androidx.core.text.getSpans
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import kotlin.math.roundToInt

/**
 * @author ma.kolpakov
 */
internal class TextDrawer(
    private val context: Context,
    private val layout: TextLayout,
    private val text: CharSequence,
    @Px private val textHeight: Int
) : ButtonTextComponentDrawer {

    init {
        val text = text
        layout.configure {
            this.text = text
            paint.textSize = (text as? Spannable)?.let {
                val sizeSpan = it.getSpans<AbsoluteSizeSpan>(0, it.length)
                if (sizeSpan.isNotEmpty()) {
                    sizeSpan.maxOf { max -> max.size }.toFloat()
                } else {
                    textHeight.toFloat()
                }
            } ?: textHeight.toFloat()
            paint.typeface = TypefaceManager.getRobotoRegularFont(context)
            includeFontPad = false
            alignment = Layout.Alignment.ALIGN_CENTER
            maxLines = text.count { it == '\n' } + 1
            layoutWidth = null
        }
    }

    override var isVisible = true

    override val width: Float
        get() = layout.width.toFloat()

    override var maxWidth: Float
        get() = layout.maxWidth?.toFloat() ?: Float.MAX_VALUE
        set(value) {
            layout.configure { maxWidth = value.roundToInt() }
        }

    override fun measureText(text: String) =
        layout.getDesiredWidth(text).toFloat()

    override val height: Float
        get() = layout.height.toFloat()

    override fun setTint(color: Int) =
        if (layout.textPaint.color == color) {
            false
        } else {
            layout.textPaint.color = color
            true
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
}