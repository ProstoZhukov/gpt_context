package ru.tensor.sbis.design.navigation.view.view.tabmenu.item

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.navigation.R
import ru.tensor.sbis.design.navigation.view.view.navmenu.NavViewSharedStyle.Companion.STATES
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.R as RDesign

/**
 * Общие среди элементов меню объекты для рисования. Стили элементов одинаковы.
 *
 * @author ma.kolpakov
 */
internal class TabMenuItemSharedPaints(
    private val context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.tabNavStyle,
    @StyleRes defStyleRes: Int = R.style.TabNavView,
    private val isHorizontal: Boolean
) {

    /** Используется ли навигационный шрифт для иконок пунктов. */
    var isUsedNavigationIcons: Boolean = false
        set(value) {
            field = value
            loadStyles()
        }

    /** @SelfDocumented */
    @Dimension
    val width = context.resources.getDimension(
        if (isHorizontal)
            R.dimen.tab_navigation_menu_item_content_width
        else
            R.dimen.tab_navigation_menu_vertical_width
    )

    /** @SelfDocumented */
    @Dimension
    val height = context.resources.getDimension(
        if (isHorizontal)
            RDesign.dimen.tab_navigation_menu_horizontal_height
        else
            R.dimen.tab_navigation_menu_item_height
    )

    /** @SelfDocumented */
    @Dimension
    var counterTopMargin = 0f

    /** @SelfDocumented */
    @Dimension
    val textBottomMargin = context.getDimen(RDesign.attr.offset_s)

    /** @SelfDocumented */
    val iconPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    /** @SelfDocumented */
    lateinit var iconColors: ColorStateList
        private set

    /** @SelfDocumented */
    @Dimension
    var iconTopPadding = 0f

    /** @SelfDocumented */
    val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = TypefaceManager.getRobotoRegularFont(context)
    }

    /** @SelfDocumented */
    lateinit var textColors: ColorStateList
        private set

    /** @SelfDocumented */
    @Dimension
    val textStartPadding = context.getDimen(if (isHorizontal) RDesign.attr.offset_3xs else RDesign.attr.offset_xs)

    /** @SelfDocumented */
    val textEndPadding = context.getDimen(RDesign.attr.offset_3xs)

    /** @SelfDocumented */
    private val markerPaint = Paint()

    init {
        context.withStyledAttributes(
            attrs,
            R.styleable.TabNavView,
            defStyleAttr,
            defStyleRes
        ) {
            loadStyles()
        }
    }

    /** @SelfDocumented */
    @Dimension
    fun getCounterLeftPadding(@Px counterWidth: Int) =
        (width - counterWidth) * 0.9F

    private fun loadStyles() {
        loadIconStyle()
        loadTextStyle()
        loadMarkerStyle()
        counterTopMargin = context.getDimen(
            when {
                !isUsedNavigationIcons && isHorizontal -> RDesign.attr.offset_2xs
                isUsedNavigationIcons && isHorizontal -> RDesign.attr.offset_3xs
                isUsedNavigationIcons && !isHorizontal -> RDesign.attr.offset_xs
                else -> RDesign.attr.offset_s
            }
        )
        iconTopPadding =
            context.getDimen(
                when {
                    !isUsedNavigationIcons && isHorizontal -> RDesign.attr.offset_2xs
                    isUsedNavigationIcons && !isHorizontal -> RDesign.attr.offset_m
                    else -> RDesign.attr.offset_s
                }
            )
    }

    private fun loadIconStyle() {
        if (isUsedNavigationIcons) {
            iconPaint.typeface = TypefaceManager.getSbisNavigationIconTypeface(context)
            iconPaint.textSize = IconSize.X3L.getDimen(context)
        } else {
            iconPaint.typeface = TypefaceManager.getSbisMobileIconTypeface(context)
            iconPaint.textSize = IconSize.X7L.getDimen(context)
        }

        iconColors = ColorStateList(
            STATES,
            intArrayOf(
                context.getThemeColorInt(RDesign.attr.horizontalItemColorNavigationPanelsAccordion),
                context.getThemeColorInt(RDesign.attr.horizontalItemActiveColorNavigationPanelsAccordion)
            )
        )
    }

    private fun loadTextStyle() {
        textPaint.textSize = FontSize.X3S.getScaleOffDimenPx(context).toFloat()
        textColors = ColorStateList(
            STATES,
            intArrayOf(
                context.getThemeColorInt(RDesign.attr.horizontalItemColorNavigationPanelsAccordion),
                context.getThemeColorInt(RDesign.attr.horizontalItemActiveColorNavigationPanelsAccordion)
            )
        )
    }

    private fun loadMarkerStyle() {
        markerPaint.color = context.getThemeColorInt(RDesign.attr.navigationMarkerColor)
    }
}