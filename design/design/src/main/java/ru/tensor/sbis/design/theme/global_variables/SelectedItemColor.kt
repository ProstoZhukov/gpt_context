package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.SelectedItemColorModel

/**
 * Линейка цветов выбранного элемента из глобальных переменных.
 *
 * Реализует [SelectedItemColorModel].
 *
 * @author mb.kruglova
 */
enum class SelectedItemColor(
    @AttrRes private val colorAttrRes: Int
) : SelectedItemColorModel {

    /**
     * Основной цвет.
     */
    DEFAULT(R.attr.itemSelectedColor);

    override val globalVar = this

    /**
     * @SelfDocumented
     */
    @ColorInt
    fun getValue(context: Context) = ThemeTokensProvider.getColorInt(context, colorAttrRes)
}