package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.ElevationModel
import ru.tensor.sbis.design.theme.utils.getDimen
import ru.tensor.sbis.design.theme.utils.getDimenPx

/**
 * Линейка высот view над плоскостью из глобальных переменных.
 *
 * Реализует [ElevationModel].
 *
 * @author mb.kruglova
 */
enum class Elevation(
    @AttrRes private val dimenAttrRes: Int
) : ElevationModel {

    XS(R.attr.elevation_xs),
    S(R.attr.elevation_s),
    M(R.attr.elevation_m),
    L(R.attr.elevation_l),
    XL(R.attr.elevation_xl);

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