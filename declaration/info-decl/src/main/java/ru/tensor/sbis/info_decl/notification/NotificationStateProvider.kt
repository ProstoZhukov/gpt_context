package ru.tensor.sbis.info_decl.notification

import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.settings_screen_decl.SettingsDataSource

/**
 * Поставщик источника данных о состоянии настроек уведомлений
 *
 * @author ev.grigoreva
 */
interface NotificationEnabledStateProvider : Feature {

    /**
     * Получить источник данных
     */
    fun getNotificationEnabledStateDataSource(): NotificationEnabledStateDataSource
}

/**
 * Источник данных о состоянии настроек уведомлений
 */
interface NotificationEnabledStateDataSource: SettingsDataSource<String?>