package ru.tensor.sbis.design.checkbox.drawers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.TextPaint
import androidx.annotation.Px
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.utils.StaticLayoutConfigurator
import ru.tensor.sbis.design.custom_view_tools.utils.getTextWidth

/**
 * Реализация [CheckboxContentDrawer], когда контент чекбокса представляет собой шрифтовую иконку
 *
 * @author mb.kruglova
 */
internal class IconTextContentDrawer(
    private val icon: CharSequence,
    @Px private val iconSize: Int,
    context: Context
) : CheckboxContentDrawer {

    private val iconTypeface: Typeface? = TypefaceManager.getCbucIconTypeface(context)

    private val layout: Layout = run {
        val currentTypeface = if (icon.length == 1 && !Character.isLetterOrDigit(icon[0])) iconTypeface else null
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = iconSize.toFloat()
            typeface = currentTypeface
            this.color = color
        }
        StaticLayoutConfigurator.createStaticLayout(icon, textPaint) {
            includeFontPad = false
            alignment = Layout.Alignment.ALIGN_CENTER
            maxLines = Int.MAX_VALUE
            width = textPaint.getTextWidth(icon)
        }
    }

    override val width: Float
        get() = layout.width.toFloat()

    override val height: Float
        get() = layout.height.toFloat()

    override fun setTint(color: Int) = layout.run {
        if (paint.color != color) paint.color = color
    }

    override fun draw(canvas: Canvas) {
        layout.draw(canvas)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IconTextContentDrawer

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