package ru.tensor.sbis.widget_player.converter.attributes.resource

import ru.tensor.sbis.widget_player.converter.attributes.store.AttributesStore

/**
 * Ресурс виджета.
 *
 * @property value содержимое ресурса
 * @property attributes набор атрибутов ресурса
 *
 * @author am.boldinov
 */
interface WidgetResource {

    val value: ResourceValue<*>

    val attributes: AttributesStore
}