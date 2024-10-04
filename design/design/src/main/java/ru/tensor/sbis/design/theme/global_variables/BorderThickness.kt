package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.BorderThicknessModel
import ru.tensor.sbis.design.theme.utils.getDimen
import ru.tensor.sbis.design.theme.utils.getDimenPx

/**
 * Линейка толщины обводки из глобальных переменных.
 *
 * Реализует [BorderThicknessModel].
 *
 * @author mb.kruglova
 */
enum class BorderThickness(
    @AttrRes private val dimenAttrRes: Int
) : BorderThicknessModel {

    S(R.attr.borderThickness_s),
    M(R.attr.borderThickness_m),
    L(R.attr.borderThickness_l),
    X3L(R.attr.borderThickness_3xl);

    override val globalVar = this

    /**
     * @see Context.getDimen
     */
    @Dimension
    fun getDimen(context: Context) = ThemeTokensProvider.getDimen(context, dimenAttrRes)

    /**
     * @see Context.getDimenPx
     */
    @Dimension
    fun getDimenPx(context: Context) = ThemeTokensProvider.getDimenPx(context, dimenAttrRes)
}