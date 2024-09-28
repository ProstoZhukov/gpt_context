package ru.tensor.sbis.user_activity_track.activity

import android.app.Activity

/**
 * Интерфейс, предназначенный для маркировки объектов,
 * которые должны участвовать в трекинг активности пользователя.
 * Если вешается на [Activity], то нужно убедиться, что на уровне приложения используется [UserActivityTrackingWatcher].
 *
 * @author kv.martyshenko
 */
interface UserActivityTrackable {

    /**
     * Требуется ли трекать активность на данном экране.
     */
    val isTrackActivityEnabled: Boolean

    /**
     * Название экрана.
     * Используется как уникальный идентификатор экрана.
     */
    val screenName: String

}