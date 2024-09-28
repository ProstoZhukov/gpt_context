package ru.tensor.sbis.pushnotification.controller.base

import android.content.Context
import android.os.Bundle
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.controller.HandlingResult
import ru.tensor.sbis.pushnotification.controller.base.notifier.SinglePushNotifier
import ru.tensor.sbis.pushnotification.notification.PushNotification
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage
import ru.tensor.sbis.pushnotification.util.PushLogger

/**
 * Базовый класс обработчика пуш-уведомлений, который публикует все поступающие сообщения в один пуш в шторке,
 * таким образом каждое новое сообщение заменяет предыдущее.
 *
 * У каждого наследника свой пуш, разные обработчики не пересекаются и публикуют уведомления под собственным тегом.
 * К примеру - зарегистрированы 2 обработчика-наследника, по сообщениям и новостям, при публикации нового сообщения и
 * новой новости в шторке окажется 2 пуша, при появлении еще одного сообщения информация в предыдущем пуше по сообщению
 * обновится со звуковым сигналом.
 *
 * @author am.boldinov
 */
abstract class SinglePushNotificationController(context: Context) : AbstractNotificationController(context) {

    private val notifier = SinglePushNotifier(getNotifyTag())

    final override fun getNotifyTag(): String {
        return javaClass.name
    }

    final override fun handle(messages: List<PushNotificationMessage>): HandlingResult {
        messages.forEach { message ->
            createNotification(message)
                ?.let { notification ->
                    notifier.notify(notificationManager, notification)
                }
                ?: kotlin.run {
                    PushLogger.event("Cannot create push notification for type ${message.type} in controller $this")
                }
        }
        return HandlingResult(notifyDisplayed = messages)
    }

    override fun cancelAll(type: PushType) {
        notifier.cancel(notificationManager)
    }

    override fun cancel(type: PushType, params: Bundle) {
        cancelAll(type)
    }

    /**
     * Создает подготовленную и стилизованную для показа модель уведомления на основе данных пуш-сообщения.
     */
    protected abstract fun createNotification(message: PushNotificationMessage): PushNotification?
}

