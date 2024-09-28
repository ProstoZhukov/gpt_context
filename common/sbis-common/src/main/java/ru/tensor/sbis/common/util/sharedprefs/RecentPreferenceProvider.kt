package ru.tensor.sbis.common.util.sharedprefs

import android.content.Context
import android.content.SharedPreferences

/**
 * Реализация провайдера недавних значений с хранением их в SharedPreferences.
 * Хранилище для значений идентифицируется парой storageKey и capacity. Хранилище
 * может содержать не более capacity элементов.
 *
 * Алгоритм хранения: каждое недавнее значение записывается в shared prefs под отдельный
 * ключ, который формируется как название настройки + индекс элемента массива. Так же в shared prefs
 * сохраняется индекс, под которым нужно осуществить запись следующего значения. При помощи
 * закольцовывания индекса (nextIndex = (curIndex + 1) % capacity) обеспечивается вытеснение
 * устаревших значений.
 *
 * @author am.boldinov
 */
abstract class RecentPreferenceProvider<T>(
        /**
         * Объект для синхронизации изменений.
         */
        private val mutex: Any,
        /**
         * SharedPreferences, в которых хранятся недавние значения.
         */
        private val preferences: SharedPreferences,
        /**
         * Ключ для идентификации хранилища внутри SharedPreferences.
         */
        storageKey: String,
        /**
         * Количество значений, которые могут содержаться в хранилище.
         */
        private val capacity: Int
) : RecentProvider<T> {

    /**
     * Префикс для всех ключей, связанных с данным preference.
     */
    private val preferenceKeyPrefix = "${storageKey}_$capacity"

    /**
     * Ключ для хранения индекса, под который нужно положить следующий элемент.
     */
    private val pushIndexKey = "$preferenceKeyPrefix.push_index"

    /**
     * Префикс ключа для хранения недавнего значения.
     */
    private val recentValueKey = "$preferenceKeyPrefix.recent_value"

    init {
        if (capacity < 1) {
            throw IllegalArgumentException("Non-positive value $capacity specified for capacity")
        }
    }

    constructor(mutex: Any, context: Context, name: String, preferenceKey: String, capacity: Int) : this(
            mutex, context.getSharedPreferences(name, Context.MODE_PRIVATE),
            preferenceKey, capacity
    )

    override fun push(value: T) {
        synchronized(mutex) {
            val pushIndex = preferences.getInt(pushIndexKey, 0)
            val editor = preferences.edit()
            put(editor, "${recentValueKey}_$pushIndex", value)
            editor.putInt(pushIndexKey, (pushIndex + 1) % capacity)
            editor.apply()
        }
    }

    override fun contains(value: T): Boolean {
        for (preference in preferences.all) {
            if (preference.key.startsWith(recentValueKey)) {
                if (value == preference.value) {
                    return true
                }
            }
        }
        return false
    }

    override fun clear() {
        synchronized(mutex) {
            val editor = preferences.edit()
            for (preference in preferences.all) {
                if (preference.key.startsWith(recentValueKey)) {
                    editor.remove(preference.key)
                }
            }
            editor.apply()
        }
    }

    /**
     * Положить значение под указанным ключем в editor.
     */
    protected abstract fun put(editor: SharedPreferences.Editor, key: String, value: T)

}