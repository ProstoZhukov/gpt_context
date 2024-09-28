package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.ImageSizeModel
import ru.tensor.sbis.design.theme.utils.getDimen
import ru.tensor.sbis.design.theme.utils.getDimenPx

/**
 * Линейка размеров изображения из глобальных переменных.
 *
 * Реализует [ImageSizeModel].
 *
 * @author mb.kruglova
 */
enum class ImageSize(
    @AttrRes private val dimenAttrRes: Int
) : ImageSizeModel {

    X2S(R.attr.size_2xs_image),
    XS(R.attr.size_xs_image),
    S(R.attr.size_s_image),
    M(R.attr.size_m_image),
    L(R.attr.size_l_image),
    XL(R.attr.size_xl_image),
    X2L(R.attr.size_2xl_image);

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