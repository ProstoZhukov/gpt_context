package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.PaletteIntenseModel

/**
 * Линейка бледных дополнительных цветов.
 *
 * @author da.zolotarev
 */
enum class PalettePaleColor(
    @AttrRes private val colorAttrRes: Int
) : PaletteIntenseModel, PaletteColor {

    PALLETTE_1_5(R.attr.paletteColor1_5),
    PALLETTE_2_5(R.attr.paletteColor2_5),
    PALLETTE_3_5(R.attr.paletteColor3_5),
    PALLETTE_4_5(R.attr.paletteColor4_5),
    PALLETTE_5_5(R.attr.paletteColor5_5),
    PALLETTE_6_5(R.attr.paletteColor6_5),
    PALLETTE_7_5(R.attr.paletteColor7_5),
    PALLETTE_8_5(R.attr.paletteColor8_5),
    PALLETTE_9_5(R.attr.paletteColor9_5),
    PALLETTE_10_5(R.attr.paletteColor10_5),
    PALLETTE_11_5(R.attr.paletteColor11_5),
    PALLETTE_12_5(R.attr.paletteColor12_5),
    PALLETTE_13_5(R.attr.paletteColor13_5);

    override val globalVar = this

    /**
     * @SelfDocumented
     */
    @ColorInt
    override fun getValue(context: Context) = ThemeTokensProvider.getColorInt(context, colorAttrRes)
}