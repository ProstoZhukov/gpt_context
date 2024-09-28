package ru.tensor.sbis.pushnotification.controller.base.notifier

import ru.tensor.sbis.pushnotification.notification.PushNotification
import ru.tensor.sbis.pushnotification.proxy.NotificationManagerInterface

/**
 * Интерфейс уведомителя, публикующего уведомлений.
 *
 * @author am.boldinov
 */
interface PushNotifier {

    /**
     * Опубликовать уведомление на UI
     *
     * @param manager менеджер, публикующий уведомления
     * @param notification уведомление для публикации
     */
    fun notify(manager: NotificationManagerInterface, notification: PushNotification): Int

    /**
     * Отменить опубликованное уведомление на UI
     *
     * @param manager менеджер, публикующий уведомления
     */
    fun cancel(manager: NotificationManagerInterface)
}