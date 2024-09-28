package ru.tensor.sbis.main_screen.widget.storage

import android.content.Context

/**
 * Хранилище настроек главного экрана.
 *
 * @property context
 *
 * @author kv.martyshenko
 */
internal class MainScreenStorage(
    private val context: Context
) {
    private val prefs by lazy {
        context.getSharedPreferences(PREFS_TITLE, Context.MODE_PRIVATE)
    }

    /**
     * Сохраняем значени по ключу
     *
     * @param key
     * @param value
     */
    fun saveString(key: String, value: String) {
        prefs
            .edit()
            .putString(key, value)
            .apply()
    }

    /**
     * Получение значения по ключу
     *
     * @param key
     */
    fun getString(key: String): String? {
        return prefs.getString(key, null)
    }

    /**
     * Сбросить все значения
     */
    fun reset() {
        prefs
            .edit()
            .clear()
            .apply()
    }

    companion object {
        private const val PREFS_TITLE = "main_screen_settings"
    }
}