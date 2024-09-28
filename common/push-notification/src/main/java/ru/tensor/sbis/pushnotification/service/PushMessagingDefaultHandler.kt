package ru.tensor.sbis.pushnotification.service

import ru.tensor.sbis.push_cloud_messaging.dispatcher.PushMessagingHandler
import ru.tensor.sbis.pushnotification.center.PushCenter
import ru.tensor.sbis.pushnotification.repository.PushNotificationRepository

/**
 * @author am.boldinov
 */
internal class PushMessagingDefaultHandler(
    private val pushCenter: PushCenter,
    private val repository: PushNotificationRepository
) : PushMessagingHandler {

    override fun onMessageReceived(message: Map<String, String>) {
        pushCenter.handleMessage(message)
    }

    override fun onNewToken(token: String) {
        repository.setToken(token)
    }
}