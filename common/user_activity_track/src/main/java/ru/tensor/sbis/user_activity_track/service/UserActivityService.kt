package ru.tensor.sbis.user_activity_track.service

import androidx.annotation.AnyThread
import androidx.annotation.UiThread
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Класс, отвечающий за трекинг активности пользователя
 *
 * @author kv.martyshenko
 */
interface UserActivityService : Feature {
    /**
     * Метод для запуска мониторинга активности на экране
     *
     * @param screenName уникальный идентификатор экрана
     */
    @UiThread
    fun startPeriodicallyRegisterActivity(screenName: String)

    /**
     * Метод для остановки мониторинга активности на экране
     *
     * @param screenName уникальный идентификатор экрана
     */
    @UiThread
    fun stopPeriodicallyRegisterActivity(screenName: String)

    /**
     * Метод для регистрации разовой активности
     *
     * @param action действие ассоциированное с активностью
     */
    @AnyThread
    fun registerOneTimeActivity(action: String)

    /**
     * Сбрасывает текущее состояние мониторинга активности. Мониторинг будет остановлен.
     */
    @UiThread
    fun reset()

}