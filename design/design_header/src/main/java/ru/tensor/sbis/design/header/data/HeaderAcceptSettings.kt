package ru.tensor.sbis.design.header.data

import androidx.annotation.StringRes

/**
 * Настройки кнопки подтверждения в шапке.
 *
 * @author ma.kolpakov
 */
sealed class HeaderAcceptSettings {
    /**
     * Кнопка с текстом.
     * @param textRes строковый ресурс с текстом кнопки
     */
    class TextAccept(@StringRes internal val textRes: Int) : HeaderAcceptSettings()

    /**
     * Кнопка с галочкой.
     */
    object IconAccept : HeaderAcceptSettings()

    /**
     * Без кнопки подтверждения.
     */
    object NoneAccept : HeaderAcceptSettings()

}
