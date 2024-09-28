package ru.tensor.sbis.crud4

/**
 * Настройки отображения индикаторов загрузки внутри списка.
 * @author ma.kolpakov
 */
interface ListLoadingIndicatorConfig {
    /**
     * Подавить отображение индикатора загрузки следующей страницы.
     */
    var suppressLoadNextIndicator: Boolean

    /**
     * Подавить отображение индикатора загрузки предыдущей страницы.
     */
    var suppressLoadPrevIndicator: Boolean
}