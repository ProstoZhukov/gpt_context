package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.TextColorModel

/**
 * Линейка цветов текста из глобальных переменных.
 *
 * Реализует [TextColorModel].
 *
 * @author mb.kruglova
 */
enum class TextColor(
    @AttrRes private val colorAttrRes: Int
) : TextColorModel {

    /**
     * Основной цвет.
     */
    DEFAULT(R.attr.textColor),

    /**
     * Контрастный цвет.
     */
    CONTRAST(R.attr.contrastTextColor),

    /**
     * Цвет гиперссылки.
     */
    LINK(R.attr.linkTextColor),

    /**
     * Цвет метки.
     */
    LABEL(R.attr.labelTextColor),

    /**
     * Цвет в режиме только для чтения.
     */
    READ_ONLY(R.attr.readonlyTextColor),

    /**
     * Цвет контрастной метки.
     */
    LABEL_CONTRAST(R.attr.labelContrastTextColor),

    /**
     * Цвет выбранного элемента.
     */
    ITEM_SELECTED(R.attr.itemSelectedTextColor),

    /**
     * Цвет текста пустого представления.
     */
    PLACEHOLDER_LIST(R.attr.placeholderTextColorList);

    override val globalVar = this

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getValue(context: Context) = ThemeTokensProvider.getColorInt(context, colorAttrRes)
}