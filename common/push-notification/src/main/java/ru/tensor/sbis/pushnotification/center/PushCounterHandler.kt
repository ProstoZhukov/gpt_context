package ru.tensor.sbis.pushnotification.center

import ru.tensor.sbis.pushnotification.controller.PushHandler
import ru.tensor.sbis.pushnotification.repository.model.PushCloudAction
import ru.tensor.sbis.pushnotification.repository.model.PushNotificationMessage
import ru.tensor.sbis.pushnotification.util.counters.AppIconCounterUpdater

/**
 * Обработчик пушей, отвечающий за изменения счетчиков
 *
 * @author ev.grigoreva
 */
internal class PushCounterHandler(
    private val appIconCounterUpdater: AppIconCounterUpdater,
    private val processActionCondition: (PushNotificationMessage) -> Boolean
) : PushHandler {

    override fun handle(rawMessage: Map<String, String>, userMessage: PushNotificationMessage?) {
        if (userMessage != null && processActionCondition(userMessage)
            && userMessage.cloudAction === PushCloudAction.NOTIFY) {
            appIconCounterUpdater.incrementCounter()
        }
    }
}