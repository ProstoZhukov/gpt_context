package ru.tensor.sbis.design.logo.api

import android.graphics.drawable.Drawable

/**
 * Интерфейс поставщика иконок для логотипа.
 *
 * @author ra.geraskin
 */
internal interface IconSource {

    /** Стандартная иконка. */
    val defaultIcon: Drawable

    /** Брендовая иконка. */
    val brandLogo: Drawable?

    /** Брендовая картинка логотипа. */
    val brandImage: Drawable?
}