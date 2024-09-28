package ru.tensor.sbis.widget_player.converter.attributes

import ru.tensor.sbis.widget_player.converter.attributes.resource.WidgetResource
import ru.tensor.sbis.widget_player.converter.attributes.store.AttributesStore
import ru.tensor.sbis.widget_player.converter.attributes.store.MapAttributesStore

/**
 * @author am.boldinov
 */
class WidgetMapAttributes internal constructor(
    private val store: AttributesStore,
    override val resource: WidgetResource? = null
) : WidgetAttributes, AttributesStore by store {

    constructor(
        map: Map<String, String>,
        resource: WidgetResource? = null
    ) : this(MapAttributesStore(map), resource)

    constructor(
        pair: Pair<String, String>
    ) : this(mapOf(pair))

    constructor(
        vararg pairs: Pair<String, String>
    ) : this(mapOf(pairs = pairs))

    companion object {

        @JvmStatic
        val EMPTY = WidgetMapAttributes(MapAttributesStore.EMPTY)
    }
}