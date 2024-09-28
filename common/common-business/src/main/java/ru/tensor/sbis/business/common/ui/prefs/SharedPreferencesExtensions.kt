@file:Suppress("NOTHING_TO_INLINE")

package ru.tensor.sbis.business.common.ui.prefs

import android.content.SharedPreferences

const val DEFAULT_STRING = ""

inline fun SharedPreferences.getStringOrDefault(key: String, defValue: String = DEFAULT_STRING) =
    getString(key, defValue) ?: defValue

inline fun SharedPreferences.getStringSetOrDefault(key: String, defValue: Set<String> = emptySet()) =
    getStringSet(key, defValue) ?: defValue

inline fun SharedPreferences.containsAll(vararg keys: String): Boolean = keys.all(this::contains)

/**
 * Безопасный вариант [valuesOf] без исключения. В случае, если значение не найдено по [name], берется [default]
 * Генерируемый [Enum.valuesOf] кидает исключение, если не найдено значение
 */
inline fun <reified E : Enum<E>> safeValueOf(name: String, default: E) =
    enumValues<E>().find { it.name == name } ?: default
