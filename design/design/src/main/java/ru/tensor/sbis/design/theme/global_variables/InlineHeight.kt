package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.AbstractHeight
import ru.tensor.sbis.design.theme.models.InlineHeightModel
import ru.tensor.sbis.design.theme.utils.getDimen
import ru.tensor.sbis.design.theme.utils.getDimenPx

/**
 * Линейка размеров строчных view из глобальных переменных.
 *
 * Реализует [InlineHeightModel].
 *
 * @author mb.kruglova
 */
enum class InlineHeight(
    @AttrRes private val dimenAttrRes: Int
) : InlineHeightModel, AbstractHeight {

    X8S(R.attr.inlineHeight_8xs),
    X7S(R.attr.inlineHeight_7xs),
    X6S(R.attr.inlineHeight_6xs),
    X5S(R.attr.inlineHeight_5xs),
    X4S(R.attr.inlineHeight_4xs),
    X3S(R.attr.inlineHeight_3xs),
    X2S(R.attr.inlineHeight_2xs),
    XS(R.attr.inlineHeight_xs),
    S(R.attr.inlineHeight_s),
    M(R.attr.inlineHeight_m),
    L(R.attr.inlineHeight_l),
    XL(R.attr.inlineHeight_xl),
    X2L(R.attr.inlineHeight_2xl),
    X3L(R.attr.inlineHeight_3xl);

    override val globalVar = this

    /**
     * @see Context.getDimen
     */
    @Dimension
    override fun getDimen(context: Context) = ThemeTokensProvider.getDimen(context, dimenAttrRes)

    /**
     * @see Context.getDimenPx
     */
    @Dimension
    override fun getDimenPx(context: Context) = ThemeTokensProvider.getDimenPx(context, dimenAttrRes)

    companion object {
        /**
         * Высота контролла по умолчанию.
         */
        val DEFAULT = M
    }
}