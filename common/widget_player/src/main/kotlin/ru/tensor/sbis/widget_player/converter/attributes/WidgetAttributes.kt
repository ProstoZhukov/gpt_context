package ru.tensor.sbis.widget_player.converter.attributes

import ru.tensor.sbis.widget_player.converter.attributes.resource.WidgetResource
import ru.tensor.sbis.widget_player.converter.attributes.store.AttributesStore

/**
 * Атрибуты виджета.
 *
 * @property resource опциональный ресурс виджета
 *
 * @author am.boldinov
 */
interface WidgetAttributes : AttributesStore {

    val resource: WidgetResource?

    companion object KeyContract {
        const val ID = "id"
    }
}