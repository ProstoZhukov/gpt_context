package ru.tensor.sbis.widget_player.converter

import ru.tensor.sbis.widget_player.converter.attributes.store.AttributesStore
import ru.tensor.sbis.widget_player.converter.element.WidgetElement

/**
 * @author am.boldinov
 */
interface WidgetElementUpdater<ELEMENT : WidgetElement> {

    suspend fun onDataRefreshed(element: ELEMENT, data: AttributesStore): Boolean

    suspend fun onDataError(element: ELEMENT, error: WidgetDataError): Boolean
}