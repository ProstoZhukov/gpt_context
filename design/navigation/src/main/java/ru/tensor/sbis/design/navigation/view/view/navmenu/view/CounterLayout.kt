package ru.tensor.sbis.design.navigation.view.view.navmenu.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.styles.StyleParams
import ru.tensor.sbis.design.navigation.R
import ru.tensor.sbis.design.navigation.view.view.navmenu.NavViewSharedStyle

/**
 * Плоская разметка для счётчиков в аккордеоне.
 *
 * @author ma.kolpakov
 */
internal class CounterLayout(
    context: Context,
    private val style: NavViewSharedStyle
) {
    private val dividerRect = Rect(0, 0, style.counterDividerWidth, style.counterDividerHeight)

    internal var counterLeftView: TextLayout =
        TextLayout.createTextLayoutByStyle(
            context,
            StyleParams.StyleKey(R.style.NavItemNewCounter, R.attr.navItemNewCounterTheme)
        )
    internal var counterRightView: TextLayout =
        TextLayout.createTextLayoutByStyle(
            context,
            StyleParams.StyleKey(R.style.NavItemTotalCounter, R.attr.navItemTotalCounterTheme)
        )

    /**
     * Признак нахождения счётчика у самого правого края аккордеона (отсутствует кнопка с иконкой). Влияет на отступ справа.
     */
    var inEdge = true

    /** @see View.layout */
    fun layout(top: Int, right: Int, bottom: Int) {
        val parentHeight = bottom - top
        val counterRightX = right - getMarginEnd() - counterRightView.width
        val dividerX = counterRightX - style.counterMargin - style.counterDividerWidth
        val counterLeftX = if (needDivider()) {
            dividerX - style.counterMargin - counterLeftView.width
        } else {
            right - getMarginEnd() - counterLeftView.width
        }

        val counterRightY = (parentHeight - counterRightView.height) / 2
        val counterLeftY = (parentHeight - counterLeftView.height) / 2

        counterRightView.layout(counterRightX, counterRightY)
        dividerRect.offsetTo(dividerX, (parentHeight - dividerRect.height()) / 2)
        counterLeftView.layout(counterLeftX, counterLeftY)
    }

    /** @SelfDocumented */
    fun getWidth(): Int {
        if (!hasRight() && !hasLeft()) return 0
        var width = style.counterMarginStart + counterLeftView.width + counterRightView.width + getMarginEnd()
        if (needDivider()) {
            width += style.counterDividerWidth + style.counterMargin + style.counterMargin
        }
        return width
    }

    /** @see View.draw */
    fun draw(canvas: Canvas) {
        counterRightView.draw(canvas)
        if (needDivider()) {
            canvas.drawRect(dividerRect, style.counterDividerPaint)
        }
        counterLeftView.draw(canvas)
    }

    private fun hasLeft() = counterLeftView.width > 0
    private fun hasRight() = counterRightView.width > 0
    private fun needDivider() = hasLeft() && hasRight()
    private fun getMarginEnd() = if (inEdge) style.counterMarginEnd else style.counterMargin
}
