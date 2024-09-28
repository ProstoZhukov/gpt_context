package ru.tensor.sbis.user_activity_track_watcher.service

import android.content.SharedPreferences

/**
 * Класс, предоставляющий доступ к настройкам мониторинга активности пользователя
 *
 * @param sharedPreferences файл настроек
 *
 * @author kv.martyshenko
 */
internal class UserActivitySettings(private val sharedPreferences: SharedPreferences) {

    /** @SelfDocumented */
    fun getLastSuccessUpdateTime(): Long {
        return sharedPreferences.getLong(LAST_SUCCESS_UPDATING_TIME_IN_MILLIS, 0)
    }

    /** @SelfDocumented */
    fun setLastSuccessUpdatingTime(time: Long) {
        sharedPreferences.edit()
            .putLong(LAST_SUCCESS_UPDATING_TIME_IN_MILLIS, time)
            .apply()
    }

    /** @SelfDocumented */
    fun clear() {
        sharedPreferences.edit()
            .remove(LAST_SUCCESS_UPDATING_TIME_IN_MILLIS)
            .apply()
    }

    internal companion object {
        private val LAST_SUCCESS_UPDATING_TIME_IN_MILLIS =
            UserActivitySettings::class.java.canonicalName + ".LAST_SUCCESS_UPDATING_TIME_IN_MILLIS"
    }

}