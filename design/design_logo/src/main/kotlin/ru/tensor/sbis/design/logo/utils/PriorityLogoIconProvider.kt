package ru.tensor.sbis.design.logo.utils

import android.graphics.drawable.Drawable
import ru.tensor.sbis.design.logo.api.IconProvider
import ru.tensor.sbis.design.logo.api.IconSource

/**
 * Провайдер иконки для логотипа.
 * Осуществляет выбор иконки по приоритету.
 *
 * @author ra.geraskin
 */
internal class PriorityLogoIconProvider(private val iconSource: IconSource) : IconProvider {

    /**
     * Получить иконку в порядке приоритета.
     *
     * 1 - Иконка-картинка бренда, которая заменит весь компонент целиком.
     *
     * 2 - Иконка логотипа, которая будет использоваться вместо птицы.
     *      Не окрашивается в брендовые цвета в зависимости от стиля.
     *      Не отображается кружок под иконкой в аккордеоне.
     *
     * 3 - Иконка логотипа, переданная в компонент через API.
     *      Окрашивается в брендовые цвета в зависимости от стиля.
     *      Отображается кружок под иконкой в аккордеоне.
     *
     * 4 - Стандартная иконка птицы.
     *      Окрашивается в брендовые цвета в зависимости от стиля.
     *      Отображается кружок под иконкой в аккордеоне.
     *
     * @param apiIcon Drawable иконка, полученная из API компонента.
     */
    override fun getIcon(apiIcon: Drawable?): LogoIcon {
        val brandImage = iconSource.brandImage
        val brandLogo = iconSource.brandLogo
        return when {
            brandImage != null -> LogoIcon.BrandImage(brandImage)
            brandLogo != null -> LogoIcon.BrandLogo(brandLogo)
            apiIcon != null -> LogoIcon.DefaultIcon(apiIcon)
            else -> LogoIcon.DefaultIcon(iconSource.defaultIcon)
        }
    }

}