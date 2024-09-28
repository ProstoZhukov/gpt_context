package ru.tensor.sbis.review.triggers

import android.content.SharedPreferences

/**
 * Интерфейс сериализатора событий. Определяется для каждого типа триггера
 *
 * @author ma.kolpakov
 */
interface EventSerializer {

    /**
     * Метод сохранения события может отличаться для разных типов триггеров
     */
    fun serialize(eventKey: String, storage: SharedPreferences)
}
