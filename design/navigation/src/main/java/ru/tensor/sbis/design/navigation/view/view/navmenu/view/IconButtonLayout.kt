package ru.tensor.sbis.design.navigation.view.view.navmenu.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.styles.StyleParams
import ru.tensor.sbis.design.navigation.R
import ru.tensor.sbis.design.navigation.view.view.navmenu.NavViewSharedStyle

/**
 * Плоская разметка для иконки кнопки в аккордеоне.
 *
 * @author ma.kolpakov
 */
internal class IconButtonLayout(context: Context, private val style: NavViewSharedStyle) {
    private val radius = style.iconButtonCornerRadius
    private val backgroundRect = RectF()

    private val buttonBackgroundPaint = Paint().apply {
        color = this@IconButtonLayout.style.iconButtonBackgroundColor
    }

    /** @SelfDocumented */
    internal var iconView: TextLayout =
        TextLayout.createTextLayoutByStyle(
            context,
            StyleParams.StyleKey(R.style.WidgetIcon)
        ).apply {
            configure {
                colorStateList = ColorStateList.valueOf(style.iconButtonColor)
                paint.typeface = TypefaceManager.getSbisMobileIconTypeface(context)
                includeFontPad = false
            }
            setTouchPadding(style.iconButtonTouchPadding)
        }

    /** @see View.layout */
    fun layout(top: Int, right: Int, bottom: Int) {
        if (!isVisible()) return
        val topBackgroundPos = ((bottom - top) - style.iconButtonSize) / 2f
        val leftBackgroundPos = (right - style.iconButtonSize - style.iconButtonMarginEnd).toFloat()
        val leftIconPos = leftBackgroundPos.toInt() + (style.iconButtonSize - iconView.width) / 2
        val topIconPos = topBackgroundPos.toInt() + (style.iconButtonSize - iconView.height) / 2
        backgroundRect.set(
            leftBackgroundPos,
            topBackgroundPos,
            leftBackgroundPos + style.iconButtonSize,
            topBackgroundPos + style.iconButtonSize
        )
        iconView.layout(leftIconPos, topIconPos)
    }

    /** @SelfDocumented */
    fun getWidth() = if (isVisible()) style.iconButtonSize + style.iconButtonMarginEnd else 0

    /** @see View.draw */
    fun draw(canvas: Canvas) {
        if (!isVisible()) return
        canvas.drawRoundRect(backgroundRect, radius, radius, buttonBackgroundPaint)
        iconView.draw(canvas)
    }

    /** @SelfDocumented */
    fun setIcon(icon: String) {
        iconView.configure {
            text = icon
        }
    }

    private fun isVisible() = iconView.text.isNotBlank()
}