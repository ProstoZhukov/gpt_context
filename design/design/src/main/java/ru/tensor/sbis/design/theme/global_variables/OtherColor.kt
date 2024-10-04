package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.OtherColorModel

/**
 * Линейка цветов без категории из глобальных переменных.
 *
 * Реализует [OtherColorModel].
 *
 * @author mb.kruglova
 */
enum class OtherColor(
    @AttrRes private val colorAttrRes: Int
) : OtherColorModel {

    /**
     * Цвет тени.
     */
    SHADOW(R.attr.shadowColor),

    /**
     * Брендовый цвет.
     */
    BRAND(R.attr.brandColor);

    override val globalVar = this

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getValue(context: Context) = ThemeTokensProvider.getColorInt(context, colorAttrRes)
}