package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.PaletteIntenseModel

/**
 * Линейка бледно-насыщенных дополнительных цветов.
 *
 * @author da.zolotarev
 */
enum class PalettePaleIntenseColor(
    @AttrRes private val colorAttrRes: Int
) : PaletteIntenseModel, PaletteColor {

    PALLETTE_1_4(R.attr.paletteColor1_4),
    PALLETTE_2_4(R.attr.paletteColor2_4),
    PALLETTE_3_4(R.attr.paletteColor3_4),
    PALLETTE_4_4(R.attr.paletteColor4_4),
    PALLETTE_5_4(R.attr.paletteColor5_4),
    PALLETTE_6_4(R.attr.paletteColor6_4),
    PALLETTE_7_4(R.attr.paletteColor7_4),
    PALLETTE_8_4(R.attr.paletteColor8_4),
    PALLETTE_9_4(R.attr.paletteColor9_4),
    PALLETTE_10_4(R.attr.paletteColor10_4),
    PALLETTE_11_4(R.attr.paletteColor11_4),
    PALLETTE_12_4(R.attr.paletteColor12_4),
    PALLETTE_13_4(R.attr.paletteColor13_4);

    override val globalVar = this

    /**
     * @SelfDocumented
     */
    @ColorInt
    override fun getValue(context: Context) = ThemeTokensProvider.getColorInt(context, colorAttrRes)
}