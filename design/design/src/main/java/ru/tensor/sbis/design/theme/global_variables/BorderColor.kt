package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.BorderColorModel

/**
 * Линейка цветов обводки из глобальных переменных.
 *
 * Реализует [BorderColorModel].
 *
 * @author mb.kruglova
 */
enum class BorderColor(
    @AttrRes private val colorAttrRes: Int
) : BorderColorModel {

    /**
     * Основной цвет обводки.
     */
    DEFAULT(R.attr.borderColor),

    /**
     * Цвет обводки в режиме только для чтения.
     */
    READ_ONLY(R.attr.readonlyBorderColor),

    /**
     * Бледный цвет обводки.
     */
    PALE(R.attr.paleBorderColor);

    override val globalVar = this

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getValue(context: Context) = ThemeTokensProvider.getColorInt(context, colorAttrRes)
}