package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.OtherSizeModel
import ru.tensor.sbis.design.theme.utils.getDimen
import ru.tensor.sbis.design.theme.utils.getDimenPx

/**
 * Линейка размеров без категории из глобальных переменных.
 *
 * Реализует [OtherSizeModel].
 *
 * @author mb.kruglova
 */
enum class OtherSize(
    @AttrRes private val dimenAttrRes: Int
) : OtherSizeModel {
    HEIGHT_L_BIG_SEPARATOR(R.attr.height_l_bigSeparator),
    WIDTH_L_BIG_SEPARATOR(R.attr.width_l_bigSeparator);

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