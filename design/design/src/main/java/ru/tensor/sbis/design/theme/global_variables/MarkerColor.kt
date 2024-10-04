package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.MarkerColorModel

/**
 * Линейка цветов маркера из глобальных переменных.
 *
 * Реализует [MarkerColorModel].
 *
 * @author mb.kruglova
 */
enum class MarkerColor(
    @AttrRes private val colorAttrRes: Int
) : MarkerColorModel {

    /**
     * Цвет маркера.
     */
    DEFAULT(R.attr.markerColor),

    /**
     * Цвет маркера в режиме только для чтения.
     */
    READ_ONLY(R.attr.readonlyMarkerColor),

    /**
     * Второстепенный цвет маркера для выбранного элемента.
     */
    SECONDARY_SELECTED_ITEM(R.attr.secondaryMarkerSelectedItemColor);

    override val globalVar = this

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getValue(context: Context) = ThemeTokensProvider.getColorInt(context, colorAttrRes)
}