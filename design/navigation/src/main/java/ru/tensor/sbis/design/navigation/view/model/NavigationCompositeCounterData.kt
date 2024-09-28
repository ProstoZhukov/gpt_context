package ru.tensor.sbis.design.navigation.view.model

/**
 * Модель данных составного счётчика.
 *
 * @author ma.kolpakov
 */
internal data class NavigationCompositeCounterData(

    /**
     * Значение счётчика.
     */
    val count: Int,

    /**
     * Является ли значение счётчика основным или второстепенным.
     */
    val isSecondary: Boolean
)
