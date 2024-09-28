package ru.tensor.sbis.design.navigation.view.view.tabmenu.item

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.calendar_date_icon.CalendarDateIcon
import ru.tensor.sbis.calendar_date_icon.CalendarDateIconConfiguration
import ru.tensor.sbis.design.counters.sbiscounter.SbisCounterDrawable
import ru.tensor.sbis.design.counters.sbiscounter.SbisCounterUseCase
import ru.tensor.sbis.design.navigation.R
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel

/**
 * Логика [HorizontalTabItemView] и [VerticalTabItemView].
 * @author ma.kolpakov
 */
internal class TabItemViewController(
    override var paints: TabMenuItemSharedPaints
) : TabItemViewApi {

    private lateinit var view: View

    /** @SelfDocumented */
    lateinit var calendarDrawable: CalendarDateIcon
        private set

    /** @SelfDocumented */
    lateinit var counterDrawable: SbisCounterDrawable
        private set

    override var viewLabel: NavigationItemLabel? = null
        set(value) {
            field = value
            view.requestLayout()
        }

    override var icon = ""
        set(value) {
            if (field != value) {
                field = value
                iconWidth = paints.iconPaint.measureText(icon)
                view.invalidate()
            }
        }

    override val text
        get() = textToDraw

    override var counter
        get() = counterDrawable.count
        set(value) {
            counterDrawable.count = value
            view.invalidate()
        }

    override var counterStyle
        get() = counterDrawable.style
        set(value) {
            counterDrawable.style = value
            view.invalidate()
        }

    /** @SelfDocumented */
    @ColorInt
    var iconColor = Color.BLACK
        private set

    /** @SelfDocumented */
    @Dimension
    var iconWidth = 0F
        private set

    /** @SelfDocumented */
    @ColorInt
    var textColor = Color.BLACK
        private set

    /** @SelfDocumented */
    @Dimension
    var textWidth = 0F
        private set

    /** @SelfDocumented */
    var textToDraw = ""
        private set

    /**
     * Текст элемента меню для специальных возможностей и UI тестов.
     */
    val accessibilityText: String
        get() = "$text|${counterDrawable.countText}"

    /**
     * Связать [HorizontalTabItemView]/[VerticalTabItemView] к [TabItemViewController].
     */
    fun attach(view: View) {
        this.view = view
        view.id = R.id.navigation_tab_nav_item_id
        calendarDrawable = CalendarDateIcon(view.context, CalendarDateIconConfiguration.FILLED)
        counterDrawable = SbisCounterDrawable(view.context).apply {
            useCase = SbisCounterUseCase.NAVIGATION
        }
    }

    /**
     * @see View.drawableStateChanged
     */
    fun drawableStateChanged(): Boolean {
        var changed = false

        val newIconColor = paints.iconColors.getColorForState(view.drawableState, Color.BLACK)
        if (newIconColor != iconColor) {
            changed = true
            iconColor = newIconColor
        }

        val newTextColor = paints.textColors.getColorForState(view.drawableState, Color.BLACK)
        if (newTextColor != textColor) {
            changed = true
            textColor = newTextColor
        }

        calendarDrawable.setIsSelected(view.isSelected)

        return changed
    }

    override fun setIconRes(@StringRes iconRes: Int) {
        icon = if (iconRes == ResourcesCompat.ID_NULL) "" else view.resources.getString(iconRes)
    }

    override fun setIconCalendarDay(calendarDay: Int?) {
        calendarDrawable.dayNumber = calendarDay
        view.requestLayout()
    }

    override fun layout() {
        viewLabel?.let { label ->
            val availableSpace =
                view.width.coerceAtLeast(view.minimumWidth) - (paints.textStartPadding + paints.textEndPadding)
            textToDraw = label.default.getString(view.context)
            textWidth = paints.textPaint.measureText(textToDraw)
            if (textWidth > availableSpace) {
                // обычное название не помещается, попробуем короткое
                textToDraw = label.short.getString(view.context)
                textWidth = paints.textPaint.measureText(textToDraw)
            }
            if (textWidth > availableSpace) {
                // даже короткое название не поместилось, будем троеточить его
                textToDraw = TextUtils.ellipsize(
                    textToDraw,
                    paints.textPaint,
                    availableSpace,
                    TextUtils.TruncateAt.END
                ).toString()
                textWidth = paints.textPaint.measureText(textToDraw)
            }
        } ?: run {
            textToDraw = ""
            textWidth = 0F
        }
    }
}