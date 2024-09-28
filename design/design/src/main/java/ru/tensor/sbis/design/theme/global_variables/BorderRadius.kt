package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.BorderRadiusModel
import ru.tensor.sbis.design.theme.utils.getDimen
import ru.tensor.sbis.design.theme.utils.getDimenPx

/**
 * Линейка стандартных скруглений из глобальных переменных.
 *
 * Реализует [BorderRadiusModel].
 *
 * @author mb.kruglova
 */
enum class BorderRadius(
    @AttrRes private val dimenAttrRes: Int
) : BorderRadiusModel {

    X3S(R.attr.borderRadius_3xs),
    X2S(R.attr.borderRadius_2xs),
    XS(R.attr.borderRadius_xs),
    S(R.attr.borderRadius_s),
    M(R.attr.borderRadius_m),
    L(R.attr.borderRadius_l),
    XL(R.attr.borderRadius_xl);

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