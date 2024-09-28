package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.IconColorModel

/**
* Линейка цветов иконки из глобальных переменных.
*
* Реализует [IconColorModel].
*
* @author mb.kruglova
*/
enum class IconColor(
    @AttrRes private val colorAttrRes: Int
) : IconColorModel {

    /**
     * Основной цвет.
     */
    DEFAULT(R.attr.iconColor),

    /**
     * Контрастный цвет.
     */
    CONTRAST(R.attr.contrastIconColor),

    /**
     * Цвет гиперссылки.
     */
    LINK(R.attr.linkIconColor),

    /**
     * Цвет метки.
     */
    LABEL(R.attr.labelIconColor),

    /**
     * Цвет в режиме только для чтения.
     */
    READ_ONLY(R.attr.readonlyIconColor);

    override val globalVar = this

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getValue(context: Context) = ThemeTokensProvider.getColorInt(context, colorAttrRes)
}