package ru.tensor.sbis.design.container.locator

/**
 * Правила для стандартных отступов контейнера от краев экрана.
 *
 * @author ps.smirnyh
 */
data class ScreenLocatorRules(
    var defaultMarginStart: Boolean = true,
    var defaultMarginTop: Boolean = true,
    var defaultMarginEnd: Boolean = true,
    var defaultMarginBottom: Boolean = true
)
