package ru.tensor.sbis.red_button.repository.data_source

import android.content.SharedPreferences
import ru.tensor.sbis.red_button.data.RedButtonState

/**
 * Класс для работы с [SharedPreferences]
 * Инкапсулирует логику работы с [SharedPreferences] для модуля "Красная Кнопка"
 * @property preferences реализация [SharedPreferences]
 *
 * @author ra.stepanov
 */
@Suppress("KDocUnresolvedReference")
class RedButtonPreferences(private val preferences: SharedPreferences) {

    /**
     * Получение данных о заглушке из [SharedPreferences]
     */
    fun getRedButtonRefreshApp() = getPreference(RED_BUTTON_REFRESH_APP, -1)

    /**
     * Запись данных о заглушке в [SharedPreferences]
     * @param value целочисленное значение (0 или 1)
     */
    fun setRedButtonRefreshApp(value: Int) = setPreference(RED_BUTTON_REFRESH_APP, value)

    /**
     * Очистка данных о заглушке из [SharedPreferences]
     */
    fun clearRedButtonRefreshApp() = preferences.edit().remove(RED_BUTTON_REFRESH_APP).apply()

    /**
     * Получение данных о состоянии красной кнопки из [SharedPreferences]
     */
    fun getRedButtonState() = getPreference(RED_BUTTON_STATE, RedButtonState.ACCESS_DENIED.value)

    /**
     * Запись данных о состоянии красной кнопки в [SharedPreferences]
     * @param value целочисленное значение (см [RedButtonStateMapper])
     */
    fun setRedButtonState(value: Int) = setPreference(RED_BUTTON_STATE, value)

    /**
     * Запись данных в [SharedPreferences]
     * @param key ключ, под которым будет произведена запись
     * @param value целочисленное значение, которое будет записано
     */
    private fun setPreference(key: String, value: Int) = preferences.edit().putInt(key, value).apply()

    /**
     * Получение данных из [SharedPreferences]
     * @param key ключ, по которому будут извлечены данные
     * @param defaultValue значение по умолчанию, если данных по переданному ключу не оказалось
     */
    private fun getPreference(key: String, defaultValue: Int) = preferences.getInt(key, defaultValue)

    companion object {
        /** Ключ в преференсах под которым хранится метка о заглушке */
        const val RED_BUTTON_REFRESH_APP = "RED_BUTTON_REFRESH_APP"

        /** Ключ в преференсах под которым хранится метка о состоянии красной кнопки */
        const val RED_BUTTON_STATE = "RED_BUTTON_STATE"

        /** Ключ для преференсов */
        const val RED_BUTTON_PREFS = "RED_BUTTON_PREFS"
    }
}