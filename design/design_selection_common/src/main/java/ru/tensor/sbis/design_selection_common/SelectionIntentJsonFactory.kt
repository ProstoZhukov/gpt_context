package ru.tensor.sbis.design_selection_common

/**
 * Фабрика для создания intent_json для конфигурации источников на контроллере компонента выбора.
 *
 * @author vv.chekurda
 */
interface SelectionIntentJsonFactory {

    /**
     * Создать intent_json для конфигурации источников на контроллере компонента выбора.
     */
    fun createIntentJson(): String?
}