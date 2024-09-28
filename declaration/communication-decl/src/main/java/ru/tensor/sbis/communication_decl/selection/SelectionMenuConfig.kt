package ru.tensor.sbis.communication_decl.selection

import java.io.Serializable

/**
 * Конфигурация компонента меню выбора в шторке.
 *
 * @author vv.chekurda
 */
interface SelectionMenuConfig<CONFIG : SelectionConfig> : Serializable {

    /**
     * Конфигурация компонента выбора.
     */
    val selectionConfig: CONFIG

    /**
     * true, чтобы автоматически скрывать меню при пустом списке, доступных для выбора.
     */
    val autoHideEmptyMenu: Boolean
        get() = true

    /**
     * true, чтобы игнорировать инсеты.
     */
    val ignoreWindowInsets: Boolean
        get() = true

    /**
     * true, если клик вне области шторки или свайп за ручку может закрыть меню.
     */
    val closable: Boolean
        get() = false

    /**
     * true, чтобы показывать заглушки списка.
     */
    val showStubs: Boolean
        get() = false

    /**
     * true, чтобы показывать прогрессы загрузки при пагинации.
     */
    val showLoaders: Boolean
        get() = false
}