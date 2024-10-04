package ru.tensor.sbis.design.buttons.base.models.title

import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonTitleStyle
import ru.tensor.sbis.design.theme.HorizontalPosition

/**
 * Модель текста в кнопке.
 *
 * @param text Текст для кнопки.
 * @param position Положение текста относительно иконки, если есть.
 * @param size Размеры текста. Если не задан, применяется размер от стиля кнопки.
 * @param style Стиль текста в кнопке. Если не задан, применяется стиль кнопки.
 *
 * @author ma.kolpakov
 */
data class SbisButtonTitle(
    val text: CharSequence?,
    val position: HorizontalPosition = HorizontalPosition.RIGHT,
    val size: SbisButtonTitleSize? = null,
    val style: SbisButtonTitleStyle? = null,
    val scaleOn: Boolean? = null
)
