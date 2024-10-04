package ru.tensor.sbis.design.buttons.button.api

import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.button.models.SbisButtonBackground

/**
 * Описание API для управления специфичными свойствами кнопки [SbisButton].
 *
 * Базовые настройки и стилизации находятся в [AbstractButtonApi][ru.tensor.sbis.design.buttons.base.api.AbstractButtonApi].
 *
 * Настройки текста и иконки находятся в [SbisButtonIconAndTextApi][ru.tensor.sbis.design.buttons.icon_text.api.SbisButtonIconAndTextApi].
 *
 * @author ma.kolpakov
 */
interface SbisButtonApi {
    /**
     * Тип фона кнопки.
     */
    var backgroundType: SbisButtonBackground

    /**
     * Скругление кнопки.
     */
    var cornerRadiusValue: Float

    /**
     * Есть ли стандартный отступ от края кнопки. По умолчанию true.
     */
    var hasHorizontalPadding: Boolean

    /**
     * Пространство, которое занимает текст.
     * Вернет 0, если кнопка содержит только иконку или счетчик, и нет текста.
     */
    fun measureText(text: String): Float
}
