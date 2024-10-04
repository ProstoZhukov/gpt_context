package ru.tensor.sbis.design.logo.utils

import android.graphics.drawable.Drawable

/**
 * Внутренняя модель иконки компонента. Определяет то, как компонент будет рисовать иконку в логотипе.
 *
 * @author ra.geraskin
 */
internal sealed interface LogoIcon {

    /**
     * Drawable объект для отображения иконки в компоненте.
     */
    val iconDrawable: Drawable

    /**
     * Тип стандартной иконки.
     * - Окрашивается в брендовые цвета в зависимости от стиля.
     * - В аккордеоне под иконкой отображается кружок.
     */
    class DefaultIcon(override val iconDrawable: Drawable) : LogoIcon

    /**
     * Тип брендовой иконки.
     * - Не окрашивается в брендовые цвета в зависимости от стиля.
     * - В аккордеоне под иконкой НЕ отображается кружок.
     */
    class BrandLogo(override val iconDrawable: Drawable) : LogoIcon

    /**
     * Тип изображения логотипа бренда.
     * - Отображается вместо всего компонента без текста.
     */
    class BrandImage(override val iconDrawable: Drawable) : LogoIcon
}
