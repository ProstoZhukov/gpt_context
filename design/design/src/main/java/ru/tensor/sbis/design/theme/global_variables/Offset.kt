package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.OffsetModel
import ru.tensor.sbis.design.theme.utils.getDimen
import ru.tensor.sbis.design.theme.utils.getDimenPx

/**
 * Линейка стандартных отступов из глобальных переменных.
 *
 *  Реализует [OffsetModel].
 *
 * @author mb.kruglova
 */
enum class Offset(
    @AttrRes private val dimenAttrRes: Int
) : OffsetModel {

    X3S(R.attr.offset_3xs),
    X2S(R.attr.offset_2xs),
    XS(R.attr.offset_xs),
    S(R.attr.offset_s),
    ST(R.attr.offset_st),
    M(R.attr.offset_m),
    L(R.attr.offset_l),
    XL(R.attr.offset_xl),
    X2L(R.attr.offset_2xl),
    X3L(R.attr.offset_3xl);

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