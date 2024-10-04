package ru.tensor.sbis.design.buttons.base.zentheme

import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.theme.zen.ZenThemeModel
import ru.tensor.sbis.design.theme.zen.ZenThemeSupport

/**
 * Интерфейс-прослойка, между [ZenThemeSupport] и наследником. Существует исключительно, чтобы дать возможность
 * устанавливать конкретный Дзен стиль для кнопки принудительно, не опираясь на стиль, который был у кнопки изначально.
 *
 * @author ra.geraskin
 */
interface ButtonZenThemeSupport : ZenThemeSupport {

    /**
     * Установить для кнопки Дзен стиль, рассчитанный на основе Дзен модели [themeModel] и на основе полученного стиля
     * [style].
     *
     * Применим в случаях, когда для кнопки с кастомным стилем требуется применить какую-либо Дзен темизацию.
     * (Для кнопок с кастомными цветами Дзен темизация не применяется, т.к. в спецификации описаны только конкретные
     * стили, к которым применима Дзен темизация).
     *
     * [Спецификация](https://n.sbis.ru/article/fecfb715-c8d6-4616-a7a8-ce8cc27c0f52)
     *
     * @param themeModel Дзен модель с цветами.
     * @param style стиль, на который нужно опираться при расчёте нового Дзен стиля кнопки. Может быть любым.
     */
    fun setZenThemeForced(themeModel: ZenThemeModel, style: SbisButtonStyle)
}