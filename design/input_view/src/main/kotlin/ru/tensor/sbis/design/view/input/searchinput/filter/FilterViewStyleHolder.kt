package ru.tensor.sbis.design.view.input.searchinput.filter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color.MAGENTA
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.ColorUtils
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.view.input.R
import kotlin.math.roundToInt

/**
 * @author ma.kolpakov
 */
internal class FilterViewStyleHolder {
    private var states = arrayOf(
        intArrayOf(android.R.attr.state_pressed),
        intArrayOf(-android.R.attr.state_enabled),
        intArrayOf()
    )

    lateinit var filterColors: ColorStateList
    lateinit var iconColors: ColorStateList

    var dividerColor: Int = MAGENTA
    var backgroundColor: Int = MAGENTA
    var backgroundColorAdditional: Int = MAGENTA

    var filterHeight = 0
    var filterHeightSmall = 0
    var filtersTextSize = 0
    var iconHeight = 0
    var dividerHeight = 0

    var iconOffsetRight = 0
    var iconOffsetLeft = 0
    var filterOffsetLeft = 0

    lateinit var filterIcon: String
    lateinit var filterIconFiled: String

    fun initStyle(
        context: Context,
        attributeSet: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defaultStyle: Int
    ) {
        filterIcon =
            context.getString(ru.tensor.sbis.design.R.string.design_mobile_icon_filter_equal)
        filterIconFiled =
            context.getString(ru.tensor.sbis.design.R.string.design_mobile_icon_filter_selected_equal)
        val styleRes = ThemeContextBuilder(context, defStyleAttr, defaultStyle).buildThemeRes()
        context.withStyledAttributes(
            attributeSet,
            R.styleable.SbisFilterView,
            defStyleAttr,
            styleRes
        ) {
            filterHeight = context.getDimenPx(ru.tensor.sbis.design.R.attr.inlineHeight_xs)
            filterHeightSmall = context.getDimenPx(ru.tensor.sbis.design.R.attr.inlineHeight_4xs)

            // Ограничение максимального размера динамического шрифта
            filtersTextSize = context.getDimenPx(ru.tensor.sbis.design.R.attr.fontSize_xs_scaleOn)
                .coerceAtMost(context.getDimenPx(ru.tensor.sbis.design.R.attr.fontSize_xl_scaleOff))
            iconHeight = context.getDimenPx(ru.tensor.sbis.design.R.attr.iconSize_4xl)
            dividerHeight = context.getDimenPx(ru.tensor.sbis.design.R.attr.borderThickness_s)

            iconOffsetRight = context.getDimenPx(ru.tensor.sbis.design.R.attr.offset_m)
            iconOffsetLeft = context.getDimenPx(ru.tensor.sbis.design.R.attr.offset_2xs)
            filterOffsetLeft = context.getDimenPx(ru.tensor.sbis.design.R.attr.offset_s)

            val filtersTextColor =
                getColor(R.styleable.SbisFilterView_SbisFilterView_filterTextColor, MAGENTA)
            val iconTextColor =
                getColor(R.styleable.SbisFilterView_SbisFilterView_filterIconColor, MAGENTA)

            val readonlyTextColor =
                getColor(R.styleable.SbisFilterView_SbisFilterView_readonlyTextColor, MAGENTA)
            val readonlyIconColor =
                getColor(R.styleable.SbisFilterView_SbisFilterView_readonlyIconColor, MAGENTA)

            val pressedTextColor =
                getColor(R.styleable.SbisFilterView_SbisFilterView_pressedTextColor, MAGENTA)
            val pressedIconColor =
                getColor(R.styleable.SbisFilterView_SbisFilterView_pressedIconColor, MAGENTA)
            dividerColor = getColor(R.styleable.SbisFilterView_SbisFilterView_dividerColor, MAGENTA)
            backgroundColor =
                getColor(R.styleable.SbisFilterView_SbisFilterView_baseBackgroundColor, MAGENTA)
            backgroundColorAdditional =
                getColor(
                    R.styleable.SbisFilterView_SbisFilterView_additionalBackgroundColor,
                    MAGENTA
                )

            filterColors = ColorStateList(
                states,
                intArrayOf(
                    ColorUtils.setAlphaComponent(pressedTextColor, PRESSED_ALPHA.roundToInt()),
                    readonlyTextColor,
                    filtersTextColor
                )
            )

            iconColors = ColorStateList(
                states,
                intArrayOf(
                    ColorUtils.setAlphaComponent(pressedIconColor, PRESSED_ALPHA.roundToInt()),
                    readonlyIconColor,
                    iconTextColor
                )
            )
        }
    }
}

private const val PRESSED_ALPHA = Byte.MAX_VALUE * 0.6f