package ru.tensor.sbis.widget_player.converter

import ru.tensor.sbis.widget_player.converter.attributes.store.AttributesStore

/**
 * @author am.boldinov
 */
sealed interface WidgetBodyEvent {

    class BodyLoaded(val body: WidgetBody, val changes: List<DataChanged> = emptyList()) : WidgetBodyEvent

    class DataChanged(val id: WidgetID, val data: AttributesStore) : WidgetBodyEvent

    class DataError(val id: WidgetID, val error: WidgetDataError) : WidgetBodyEvent
}