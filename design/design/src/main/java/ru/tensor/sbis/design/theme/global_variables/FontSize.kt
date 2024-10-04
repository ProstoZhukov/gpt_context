package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.FontSizeModel
import ru.tensor.sbis.design.theme.utils.getDimen
import ru.tensor.sbis.design.theme.utils.getDimenPx

/**
 * Линейка размеров шрифта из глобальных переменных.
 *
 * Реализует [FontSizeModel].
 *
 * @author mb.kruglova
 */
enum class FontSize(
    @AttrRes private val scaleOffDimenAttrRes: Int,
    @AttrRes private val scaleOnDimenAttrRes: Int
) : FontSizeModel {

    X3S(R.attr.fontSize_3xs_scaleOff, R.attr.fontSize_3xs_scaleOn),
    X2S(R.attr.fontSize_2xs_scaleOff, R.attr.fontSize_2xs_scaleOn),
    XS(R.attr.fontSize_xs_scaleOff, R.attr.fontSize_xs_scaleOn),
    S(R.attr.fontSize_s_scaleOff, R.attr.fontSize_s_scaleOn),
    M(R.attr.fontSize_m_scaleOff, R.attr.fontSize_m_scaleOn),
    L(R.attr.fontSize_l_scaleOff, R.attr.fontSize_l_scaleOn),
    XL(R.attr.fontSize_xl_scaleOff, R.attr.fontSize_xl_scaleOn),
    X2L(R.attr.fontSize_2xl_scaleOff, R.attr.fontSize_2xl_scaleOn),
    X3L(R.attr.fontSize_3xl_scaleOff, R.attr.fontSize_3xl_scaleOn),
    X4L(R.attr.fontSize_4xl_scaleOff, R.attr.fontSize_4xl_scaleOn),
    X5L(R.attr.fontSize_5xl_scaleOff, R.attr.fontSize_5xl_scaleOn),
    X6L(R.attr.fontSize_6xl_scaleOff, R.attr.fontSize_6xl_scaleOn),
    X7L(R.attr.fontSize_7xl_scaleOff, R.attr.fontSize_7xl_scaleOn),
    X8L(R.attr.fontSize_8xl_scaleOff, R.attr.fontSize_8xl_scaleOn);

    override val globalVar = this

    /**
     * @see Context.getDimen
     */
    @Dimension
    fun getScaleOffDimen(context: Context) = ThemeTokensProvider.getDimen(context, scaleOffDimenAttrRes)

    /**
     * @see Context.getDimen
     */
    @Dimension
    fun getScaleOnDimen(context: Context) = ThemeTokensProvider.getDimen(context, scaleOnDimenAttrRes)

    /**
     * @see Context.getDimenPx
     */
    @Dimension
    fun getScaleOffDimenPx(context: Context) = ThemeTokensProvider.getDimenPx(context, scaleOffDimenAttrRes)

    /**
     * @see Context.getDimenPx
     */
    @Dimension
    fun getScaleOnDimenPx(context: Context) = ThemeTokensProvider.getDimenPx(context, scaleOnDimenAttrRes)

    companion object {
        /**
         * Размер шрифта по умолчанию.
         */
        val DEFAULT = ru.tensor.sbis.design.theme.global_variables.FontSize.M
    }
}