package ru.tensor.sbis.design.theme.global_variables

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.theme.ThemeTokensProvider
import ru.tensor.sbis.design.theme.models.PaletteIntenseModel

/**
 * Линейка насыщенных дополнительных цветов.
 *
 * @author da.zolotarev
 */
enum class PaletteIntenseColor(
    @AttrRes private val colorAttrRes: Int
) : PaletteIntenseModel, PaletteColor {

    PALLETTE_1_3(R.attr.paletteColor1_3),
    PALLETTE_2_3(R.attr.paletteColor2_3),
    PALLETTE_3_3(R.attr.paletteColor3_3),
    PALLETTE_4_3(R.attr.paletteColor4_3),
    PALLETTE_5_3(R.attr.paletteColor5_3),
    PALLETTE_6_3(R.attr.paletteColor6_3),
    PALLETTE_7_3(R.attr.paletteColor7_3),
    PALLETTE_8_3(R.attr.paletteColor8_3),
    PALLETTE_9_3(R.attr.paletteColor9_3),
    PALLETTE_10_3(R.attr.paletteColor10_3),
    PALLETTE_11_3(R.attr.paletteColor11_3),
    PALLETTE_12_3(R.attr.paletteColor12_3),
    PALLETTE_13_3(R.attr.paletteColor13_3);

    override val globalVar = this

    /**
     * @SelfDocumented
     */
    @ColorInt
    override fun getValue(context: Context) = ThemeTokensProvider.getColorInt(context, colorAttrRes)
}