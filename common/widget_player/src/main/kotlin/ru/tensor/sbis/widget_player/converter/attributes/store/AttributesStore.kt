package ru.tensor.sbis.widget_player.converter.attributes.store

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import java.lang.reflect.Type

/**
 * Представление набора атрибутов (key-value).
 *
 * @author am.boldinov
 */
interface AttributesStore {

    fun get(key: String): String?

    fun keySet(): Set<String>

    fun isEmpty(): Boolean
}

fun AttributesStore.getAsBoolean(key: String): Boolean? {
    return getNotEmpty(key)?.toBooleanStrictOrNull()
}

fun AttributesStore.getAsInt(key: String): Int? {
    return getNotEmpty(key)?.toIntOrNull()
}

fun AttributesStore.getNotEmpty(key: String): String? {
    return get(key).takeUnless { it.isNullOrEmpty() }
}

fun AttributesStore.getNotNull(key: String): String {
    return get(key) ?: ""
}

fun AttributesStore.getAsJsonAttributes(key: String): AttributesStore {
    return get(key)?.let {
        try {
            MapAttributesStore(GsonHolder.gson.fromJson(it, GsonHolder.type))
        } catch (e: Exception) {
            Timber.d(e, it)
            MapAttributesStore.EMPTY
        }
    } ?: MapAttributesStore.EMPTY
}

fun AttributesStore.getAsJsonAttributesList(key: String): List<AttributesStore> {
    return get(key)?.let {
        try {
            GsonHolder.gson.fromJson<List<HashMap<String, String>>>(it, GsonHolder.listType).map { attr ->
                MapAttributesStore(attr)
            }
        } catch (e: Exception) {
            Timber.d(e, it)
            emptyList()
        }
    } ?: emptyList()
}

private object GsonHolder {

    val gson = Gson()
    val type: Type = object : TypeToken<HashMap<String, String>>() {}.type
    val listType: Type = object : TypeToken<List<HashMap<String, String>>>() {}.type
}