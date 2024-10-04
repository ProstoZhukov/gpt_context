/**
 * Данные для демонстрации возможностей SbisAppBarLayout
 *
 * @author ma.kolpakov
 * Создан 9/27/2019
 */
@file:JvmName("AppBarDemoData")

package ru.tensor.sbis.appdesign.appbar

import ru.tensor.sbis.appdesign.R
import ru.tensor.sbis.design.toolbar.appbar.model.AppBarModel
import ru.tensor.sbis.design.toolbar.appbar.model.ColorBackground
import ru.tensor.sbis.design.toolbar.appbar.model.ColorModel
import ru.tensor.sbis.design.toolbar.appbar.model.ImageBackground

/**
 * Модель в "светлой расцветке"
 */
@JvmField
val MODEL_LIGHT = AppBarModel(
    ImageBackground(
        "https://klike.net/uploads/posts/2019-07/1564314090_3.jpg",
        R.drawable.company_placeholder
    ),
    ColorModel(
        "#C0B0A0",
        true
    )
)

/**
 * Модель в "темной расцветке"
 */
@JvmField
val MODEL_DARK = AppBarModel()

/**
 * Модель c цветовой заливкой
 */
@JvmField
val MODEL_COLOR = AppBarModel(
    ColorBackground(0xF00),
    ColorModel(0x0F0, false)
)


