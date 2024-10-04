package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.SeparatorColorModel

/**
 * Линейка цветов разделителя из глобальных переменных.
 *
 * Реализует [SeparatorColorModel].
 *
 * @author mb.kruglova
 */
enum class SeparatorColor(
    @AttrRes private val colorAttrRes: Int
) : SeparatorColorModel {

    /**
     * Основной цвет.
     */
    DEFAULT(R.attr.separatorColor),

    /**
     * Неакцентный цвет.
     */
    UNACCENTED(R.attr.unaccentedSeparatorColor);

    override val globalVar = this

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getValue(context: Context) = ThemeTokensProvider.getColorInt(context, colorAttrRes)
}