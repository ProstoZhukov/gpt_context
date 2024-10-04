package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.ProgressColorModel

/**
 * Линейка цветов индикатора прогресса из глобальных переменных.
 *
 * Реализует [ProgressColorModel].
 *
 * @author mb.kruglova
 */
enum class ProgressColor(
    @AttrRes private val colorAttrRes: Int
) : ProgressColorModel {

    /**
     * Основной цвет.
     */
    DEFAULT(R.attr.progressColor),

    /**
     * Контрастный цвет.
     */
    CONTRAST(R.attr.contrastProgressColor);

    override val globalVar = this

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getValue(context: Context) = ThemeTokensProvider.getColorInt(context, colorAttrRes)
}