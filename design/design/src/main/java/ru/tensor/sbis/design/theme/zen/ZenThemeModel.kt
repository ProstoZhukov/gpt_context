package ru.tensor.sbis.design.theme.zen

import androidx.annotation.ColorInt

/**
 * Модель Дзен темы.
 *
 * @property elementsColors Цвета элементов внутри дзенированного компонента
 * @property dominantColor средний цвет изображения
 * @property complimentaryColor комплиментарный цвет (обратный к доминантному на цветовом круге)
 *
 * @author da.zolotarev
 */
data class ZenThemeModel(
    val elementsColors: ZenThemeElementsColors,
    @ColorInt
    val dominantColor: Int,
    @ColorInt
    val complimentaryColor: Int
)