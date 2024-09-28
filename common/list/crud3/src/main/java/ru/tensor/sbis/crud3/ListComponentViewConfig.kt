package ru.tensor.sbis.crud3

/**
 * Настройки компонента круд списков.
 * @author ma.kolpakov
 */
interface ListComponentViewConfig {
    /**
     * Подавить отображение индикатора загрузки всего списка.
     */
    var suppressCenterLoadIndicator: Boolean

    /**
     * Подавить отображение заглушки.
     */
    var suppressStubs: Boolean
}