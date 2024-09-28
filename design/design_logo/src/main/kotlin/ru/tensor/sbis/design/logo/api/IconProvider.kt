package ru.tensor.sbis.design.logo.api

import android.graphics.drawable.Drawable
import ru.tensor.sbis.design.logo.utils.LogoIcon

/**
 * Селектор иконок для компонента логотипа.
 *
 * @author ra.geraskin
 */
internal interface IconProvider {

    /**
     *  Получить иконку.
     *  @param apiIcon дополнительный возможный вариант иконки, полученный из API.
     */
    fun getIcon(apiIcon: Drawable?): LogoIcon

}