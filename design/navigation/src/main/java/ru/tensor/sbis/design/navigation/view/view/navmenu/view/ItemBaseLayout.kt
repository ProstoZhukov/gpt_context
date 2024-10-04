package ru.tensor.sbis.design.navigation.view.view.navmenu.view

import android.content.Context
import android.graphics.Canvas
import android.view.View
import androidx.core.graphics.withTranslation
import ru.tensor.sbis.calendar_date_icon.CalendarDateIcon
import ru.tensor.sbis.calendar_date_icon.CalendarDateIconConfiguration
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.styles.StyleParams
import ru.tensor.sbis.design.navigation.R
import ru.tensor.sbis.design.navigation.view.view.navmenu.NavViewSharedStyle

/**
 * Плоская разметка для основной части элемента аккордеона (иконка заголовок кнопка дополнительного контента).
 *
 * @author ma.kolpakov
 */
internal class ItemBaseLayout(context: Context, private val style: NavViewSharedStyle) {
    private var minimizeHeight = 0

    internal var titleView: TextLayout =
        TextLayout.createTextLayoutByStyle(context, StyleParams.StyleKey(R.style.NavViewText, R.attr.navViewTextTheme))
            .apply {
                val fadingEdgeSize = style.fadeEdgeSize
                colorStateList = style.textColors
                requiresFadingEdge = fadingEdgeSize > 0
                fadeEdgeSize = fadingEdgeSize
            }

    val calendarDrawable = CalendarDateIcon(context, CalendarDateIconConfiguration.BORDER_ONLY).apply {
        setColorList(style.iconColors)
    }
    var contentExpanded: Boolean = false
    var contentVisibility = true
    var rightAlignment: Int? = null
    var iconView: TextLayout =
        TextLayout.createTextLayoutByStyle(
            context,
            StyleParams.StyleKey(R.style.NavItemIcon, R.attr.navItemIconTheme)
        ) {
            paint.typeface = style.iconTypeface
            paint.textSize = style.iconTextSize
            includeFontPad = true
        }.apply {
            colorStateList = style.iconColors
        }

    var expandContentIcon: TextLayout =
        TextLayout.createTextLayoutByStyle(
            context,
            StyleParams.StyleKey(R.style.NavItemContentIcon)
        ) {
            paint.typeface = TypefaceManager.getSbisMobileIconTypeface(context)
            includeFontPad = false
            text = context.getString(ru.tensor.sbis.design.R.string.design_mobile_icon_arrow_down_expand)
        }.apply {
            id = R.id.navigation_nav_item_expand_arrow_id
            colorStateList = style.textColors
            setTouchPadding(style.contentButtonPadding)
        }

    /** @see View.layout */
    fun layout(left: Int, top: Int, right: Int, bottom: Int) {
        val height = bottom - top
        val width = right - left
        minimizeHeight = height
        if (rightAlignment == null) {
            calendarDrawable.setBounds(left, top, calendarDrawable.intrinsicWidth, calendarDrawable.intrinsicHeight)
            iconView.layout(left + style.iconMarginStart, (height - iconView.height) / 2)
            titleView.configure {
                maxWidth = width - getIconWidth() - getContentIconWidth()
            }

            titleView.layout(left + getIconWidth(), (height - titleView.height) / 2)
        } else {
            titleView.configure {
                maxWidth = rightAlignment!!
            }

            titleView.layout(rightAlignment!! - titleView.width, (height - titleView.height) / 2)
        }

        if (contentVisibility) {
            expandContentIcon.layout(
                titleView.left + titleView.width + style.contentButtonPadding,
                (height - expandContentIcon.height) / 2
            )
        }

        expandContentIcon.rotation = if (contentExpanded) 180f else 0f
    }

    /** @see View.draw */
    fun draw(canvas: Canvas) {
        if (rightAlignment == null) {
            if (style.isUsedNavigationIcons && calendarDrawable.dayNumber != null) {
                canvas.withTranslation(
                    style.backgroundMargins + style.iconMarginStart.toFloat(),
                    ((minimizeHeight - calendarDrawable.intrinsicHeight.toFloat()) / 2)
                ) {
                    calendarDrawable.draw(canvas)
                }
            } else {
                iconView.draw(canvas)
            }
        }
        titleView.draw(canvas)
        if (contentVisibility) {
            expandContentIcon.draw(canvas)
        }
    }

    private fun getIconWidth() = style.iconMarginStart + style.iconMarginEnd +
        if (calendarDrawable.dayNumber != null) calendarDrawable.intrinsicWidth else iconView.width

    private fun getContentIconWidth() = if (contentVisibility)
        style.contentButtonPadding + expandContentIcon.width
    else 0
}
