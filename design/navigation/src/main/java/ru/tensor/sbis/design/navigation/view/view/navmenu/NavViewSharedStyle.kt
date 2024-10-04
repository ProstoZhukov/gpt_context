package ru.tensor.sbis.design.navigation.view.view.navmenu

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.ColorUtils
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.navigation.R
import ru.tensor.sbis.design.theme.global_variables.BorderRadius
import ru.tensor.sbis.design.theme.global_variables.BorderThickness
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.global_variables.InlineHeight
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt
import kotlin.math.roundToInt
import ru.tensor.sbis.design.R as RDesign

/**
 * Стиль элементов аккордеона.
 *
 * @author ma.kolpakov
 */
internal class NavViewSharedStyle(
    private val context: Context,
    @AttrRes defStyleAttr: Int = R.attr.navItemTheme,
    @StyleRes defStyleRes: Int = R.style.NavViewBase
) {
    /** Фон элемента аккордеона */
    private var backgroundColors: ColorStateList
        private set

    /** Отступы фона элемента аккордеона */
    var backgroundMargins: Int
        private set

    /** Используется ли шрифт навигационных иконок. */
    var isUsedNavigationIcons: Boolean = false

    /** Высота элемента аккордеона. */
    val itemHeight = InlineHeight.S.getDimenPx(context)

    /** @SelfDocumented */
    val iconMarginStart = Offset.M.getDimenPx(context)

    /** @SelfDocumented */
    val iconMarginEnd = Offset.L.getDimenPx(context)

    /** @SelfDocumented */
    val iconSize
        get() = if (isUsedNavigationIcons) IconSize.X6L.getDimenPx(context) else IconSize.X3L.getDimenPx(context)

    /** @SelfDocumented */
    val iconTypeface: Typeface
        get() = if (isUsedNavigationIcons) TypefaceManager.getSbisNavigationIconTypeface(context)
        else TypefaceManager.getSbisMobileIconTypeface(
            context
        )

    /** @SelfDocumented */
    val iconTextSize: Float
        get() = if (isUsedNavigationIcons) IconSize.X3L.getDimen(context)
        else IconSize.X7L.getDimen(context)

    /** Ширина маркера выделенного элемента аккордеона. */
    val selectionWidth = context.resources.getDimensionPixelSize(R.dimen.navigation_menu_selection_border_width)

    /** @SelfDocumented */
    val contentButtonPadding =
        context.getDimenPx(RDesign.attr.offset_s)

    /** @SelfDocumented */
    val counterDividerWidth = BorderThickness.S.getDimenPx(context)

    /** @SelfDocumented */
    val counterDividerHeight = context.getDimenPx(RDesign.attr.offset_m)

    /** @SelfDocumented */
    val counterMarginEnd = context.resources.getDimensionPixelSize(R.dimen.navigation_menu_counter_margin_end)

    /** @SelfDocumented */
    val counterMarginStart = context.getDimenPx(RDesign.attr.offset_m)

    /** @SelfDocumented */
    val counterMargin = context.getDimenPx(RDesign.attr.offset_2xs)

    /** @SelfDocumented */
    val verticalMarkerMargin = context.getDimen(RDesign.attr.offset_xs)

    /** @SelfDocumented */
    val iconButtonMarginEnd =
        context.resources.getDimensionPixelSize(R.dimen.navigation_menu_widget_icon_padding_default)

    /** @SelfDocumented */
    val iconButtonSize = context.resources.getDimensionPixelSize(R.dimen.navigation_menu_widget_icon_min_size)

    /** @SelfDocumented */
    var iconButtonCornerRadius = iconButtonSize / 2f
        private set

    /** Паддинг области нажатия на кнопку-иконку (кнопка диска). */
    val iconButtonTouchPadding =
        context.resources.getDimensionPixelSize(R.dimen.navigation_menu_widget_icon_padding_default)

    /** Ширина затенения текста элемента аккордеона. */
    val fadeEdgeSize = context.resources.getDimensionPixelSize(R.dimen.navigation_menu_item_fade_size)

    /** @SelfDocumented */
    lateinit var iconColors: ColorStateList
        private set

    /** @SelfDocumented */
    lateinit var textColors: ColorStateList
        private set

    /** @SelfDocumented */
    var iconButtonColor: Int = Color.MAGENTA
        private set

    var iconButtonBackgroundColor: Int = Color.MAGENTA
        private set

    /** Цвет затенения области справа от открытого аккордеона. */
    var scrimColor: Int = Color.MAGENTA
        private set

    /** @SelfDocumented */
    var selectorPaint = Paint()

    /** @SelfDocumented */
    var counterDividerPaint = Paint()

    init {
        val colors = intArrayOf(
            context.getThemeColorInt(RDesign.attr.navigationBackgroundColor),
            context.getThemeColorInt(RDesign.attr.navigationActiveBackgroundColor)
        )
        backgroundColors = ColorStateList(STATES, colors)
        backgroundMargins = Offset.X2S.getDimenPx(context)
        context.withStyledAttributes(
            null,
            R.styleable.NavView,
            defStyleAttr,
            defStyleRes
        ) {
            iconButtonCornerRadius =
                getDimension(R.styleable.NavView_navItemIconButtonCornerRadius, iconButtonCornerRadius)
            loadIconStyle(context, this)
            loadTextStyle(context)
            loadCounterDividerStyle(context)
            loadMarkerStyle(context, getResourceIdOrThrow(R.styleable.NavView_navItemSelectedBorderTheme))
        }
    }

    private fun loadIconStyle(context: Context, typedArray: TypedArray) {
        val colors = intArrayOf(
            context.getThemeColorInt(RDesign.attr.navigationIconColor),
            context.getThemeColorInt(RDesign.attr.navigationActiveIconColor)
        )
        iconButtonColor = context.getThemeColorInt(RDesign.attr.navigationSecondaryIconColor)
        iconButtonBackgroundColor =
            typedArray.getColor(R.styleable.NavView_navItemIconButtonBackgroundColor, iconButtonBackgroundColor)
        scrimColor = ColorUtils.setAlphaComponent(
            context.getThemeColorInt(RDesign.attr.dimBackgroundColor),
            SCRIM_ALPHA.roundToInt()
        )
        iconColors = ColorStateList(STATES, colors)
    }

    private fun loadTextStyle(context: Context) {
        val colors = intArrayOf(
            context.getThemeColorInt(RDesign.attr.navigationTextColor),
            context.getThemeColorInt(RDesign.attr.navigationActiveTextColor)
        )
        textColors = ColorStateList(STATES, colors)
    }

    private fun loadCounterDividerStyle(context: Context) {
        counterDividerPaint.color = context.getThemeColorInt(RDesign.attr.navigationCounterSeparatorColor)
    }

    private fun loadMarkerStyle(context: Context, @StyleRes style: Int) {
        selectorPaint.color = context.getThemeColorInt(RDesign.attr.navigationMarkerColor)
        context.withStyledAttributes(style, intArrayOf(android.R.attr.background)) {

        }
    }

    /**
     * Создать фон элементов аккордеона.
     */
    fun createBackground() = InsetDrawable(
        GradientDrawable().apply {
            color = backgroundColors
            cornerRadius = BorderRadius.X3S.getDimen(context)
        },
        backgroundMargins, 0, backgroundMargins, 0
    )

    companion object {
        internal val STATES = arrayOf(
            intArrayOf(-android.R.attr.state_selected),
            intArrayOf(android.R.attr.state_selected)
        )
        private const val SCRIM_ALPHA_PERCENT = 0.75f
        private const val SCRIM_ALPHA = Byte.MAX_VALUE * SCRIM_ALPHA_PERCENT
    }
}