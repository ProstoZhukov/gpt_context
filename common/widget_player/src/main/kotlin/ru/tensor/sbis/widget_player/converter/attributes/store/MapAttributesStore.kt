package ru.tensor.sbis.widget_player.converter.attributes.store

/**
 * @author am.boldinov
 */
@JvmInline
value class MapAttributesStore(
    private val map: Map<String, String>
) : AttributesStore {

    companion object {

        @JvmStatic
        val EMPTY = MapAttributesStore(emptyMap())
    }

    override fun get(key: String) = map[key]

    override fun keySet() = map.keys

    override fun isEmpty() = map.isEmpty()
}