package ru.tensor.sbis.info_decl.notification

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс отправителя событий прочтения уведомления
 * @author ae.noskov
 */
interface NotificationEventReadDispatcher: Feature {
    /**
     * Публикация события о прочтении уведомления
     *
     * @param notificationUuid событие прочтения уведомления
     */
    fun postEvent(notificationUuid: NotificationUUID)
}

/**
 * Интерфейс провайдера [NotificationEventReadDispatcher]
 * @author ae.noskov
 */
interface NotificationEventReadDispatcherProvider {
    /**
     * Получить экземпляр [NotificationEventReadDispatcher]
     */
    fun getNotificationEventReadDispatcher(): NotificationEventReadDispatcher
}