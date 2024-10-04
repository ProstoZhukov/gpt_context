package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.IconSizeModel
import ru.tensor.sbis.design.theme.utils.getDimen
import ru.tensor.sbis.design.theme.utils.getDimenPx

/**
 * Линейка размеров иконки из глобальных переменных.
 *
 * Реализует [IconSizeModel].
 *
 * @author mb.kruglova
 */
enum class IconSize(
    @AttrRes private val dimenAttrRes: Int,
    @AttrRes private val scaleOnDimenAttrRes: Int
) : IconSizeModel {

    X2S(R.attr.iconSize_2xs, R.attr.iconSize_2xs_scaleOn),
    XS(R.attr.iconSize_xs, R.attr.iconSize_xs_scaleOn),
    S(R.attr.iconSize_s, R.attr.iconSize_s_scaleOn),
    ST(R.attr.iconSize_st, R.attr.iconSize_st_scaleOn),
    M(R.attr.iconSize_m, R.attr.iconSize_m_scaleOn),
    L(R.attr.iconSize_l, R.attr.iconSize_l_scaleOn),
    XL(R.attr.iconSize_xl, R.attr.iconSize_xl_scaleOn),
    X2L(R.attr.iconSize_2xl, R.attr.iconSize_2xl_scaleOn),
    X3L(R.attr.iconSize_3xl, R.attr.iconSize_3xl_scaleOn),
    X4L(R.attr.iconSize_4xl, R.attr.iconSize_4xl_scaleOn),
    X5L(R.attr.iconSize_5xl, R.attr.iconSize_5xl_scaleOn),
    X6L(R.attr.iconSize_6xl, R.attr.iconSize_6xl_scaleOn),
    X7L(R.attr.iconSize_7xl, R.attr.iconSize_7xl_scaleOn);

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

    /**
     * @see Context.getDimen
     */
    @Dimension
    fun getScaleOnDimen(context: Context) = ThemeTokensProvider.getDimen(context, scaleOnDimenAttrRes)

    /**
     * @see Context.getDimenPx
     */
    @Dimension
    fun getScaleOnDimenPx(context: Context) = ThemeTokensProvider.getDimenPx(context, scaleOnDimenAttrRes)

    companion object {
        /**
         * Размер иконки по умолчанию.
         */
        val DEFAULT = L
    }
}