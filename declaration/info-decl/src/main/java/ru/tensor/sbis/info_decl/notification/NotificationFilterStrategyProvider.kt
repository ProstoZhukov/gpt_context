package ru.tensor.sbis.info_decl.notification

import ru.tensor.sbis.info_decl.model.NotificationFilterStrategy
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик стратегии фильтра уведомлений по приложению.
 *
 * @author vv.chekurda
 */
interface NotificationFilterStrategyProvider : Feature {

    /**
     * Получить стратегию формирования фильтра уведомлений.
     */
    fun getNotificationFilterStrategy(): NotificationFilterStrategy?
}