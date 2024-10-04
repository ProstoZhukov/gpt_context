package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.NavigationColorModel

/**
 * Линейка цветов навигации из глобальных переменных.
 *
 * Реализует [NavigationColorModel].
 *
 * @author mb.kruglova
 */
enum class NavigationColor(
    @AttrRes private val colorAttrRes: Int
) : NavigationColorModel {

    /**
     * Цвет разделителя в счётчике.
     */
    COUNTER_SEPARATOR(R.attr.navigationCounterSeparatorColor),

    /**
     * Цвет фона.
     */
    BACKGROUND(R.attr.navigationBackgroundColor),

    /**
     * Цвет фона выбранного раздела.
     */
    SELECTED_BACKGROUND(R.attr.navigationSelectedBackgroundColor),

    /**
     * Цвет текста раздела.
     */
    TEXT(R.attr.navigationTextColor),

    /**
     * Цвет текста активного раздела.
     */
    ACTIVE_TEXT(R.attr.navigationActiveTextColor),

    /**
     * Цвет неакцентного счётчика.
     */
    UNACCENTED_TEXT(R.attr.navigationUnaccentedTextColor),

    /**
     * Цвет акцентного счётчика.
     */
    PRIMARY_TEXT(R.attr.navigationPrimaryTextColor),

    /**
     * Цвет иконки раздела.
     */
    ICON(R.attr.navigationIconColor),

    /**
     * Цвет иконки в кнопке справа.
     */
    SECONDARY_ICON(R.attr.navigationSecondaryIconColor),

    /**
     * Цвет иконки активного раздела.
     */
    ACTIVE_ICON(R.attr.navigationActiveIconColor),

    /**
     * Цвет разделителя.
     */
    SEPARATOR(R.attr.navigationSeparatorColor),

    /**
     * Цвет маркера.
     */
    MARKER(R.attr.navigationMarkerColor),

    /**
     * Цвет фона кнопки.
     */
    BACKGROUND_BUTTON(R.attr.navigationBackgroundColorButton),

    /**
     * Цвет фона активного раздела в ННП.
     */
    HORIZONTAL_ITEM_ACTIVE_BACKGROUND_ACCORDION(R.attr.horizontalItemActiveBackgroundColorNavigationPanelsAccordion),

    /**
     * Цвет фона ННП мобильной адаптации.
     */
    HORIZONTAL_SIDEBAR_BACKGROUND(R.attr.horizontalSidebarBackgroundColorNavigationPanelsSidebar);

    override val globalVar = this

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getValue(context: Context) = ThemeTokensProvider.getColorInt(context, colorAttrRes)
}