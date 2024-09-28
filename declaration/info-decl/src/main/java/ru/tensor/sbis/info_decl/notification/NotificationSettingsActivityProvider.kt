package ru.tensor.sbis.info_decl.notification

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс для открытия экрана настроек уведомлений
 *
 * Created by am.boldinov on 28.05.2019.
 */
interface NotificationSettingsActivityProvider : Feature {

    /**
     * Возвращает Intent для открытия экрана настроек уведомлений
     */
    fun getNotificationSettingsActivityIntent(context: Context): Intent
}