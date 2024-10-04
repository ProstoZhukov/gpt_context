package ru.tensor.sbis.design.stubview.utils

import androidx.annotation.AttrRes

/**
 * _____
 *
 * @author ra.geraskin
 */
internal enum class StubViewImageColors(
    internal val lottieLairName: String,
    @AttrRes internal val colorAttrRes: Int
) {

    BORDER(
        lottieLairName = "placeholderBorderColor",
        colorAttrRes = ru.tensor.sbis.design.R.attr.placeholderBorderColor
    ),

    SKIN(
        lottieLairName = "placeholderSkinColor",
        colorAttrRes = ru.tensor.sbis.design.R.attr.placeholderSkinColor
    ),

    HAIR(
        lottieLairName = "placeholderHairColor",
        colorAttrRes = ru.tensor.sbis.design.R.attr.placeholderHairColor
    ),

    DRESS(
        lottieLairName = "placeholderDressColor",
        colorAttrRes = ru.tensor.sbis.design.R.attr.placeholderDressColor
    ),

    FILL(
        lottieLairName = "primaryContrastBackgroundColor",
        colorAttrRes = ru.tensor.sbis.design.R.attr.primaryColor
    );

}