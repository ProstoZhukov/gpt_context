package ru.tensor.sbis.push_cloud_messaging.dispatcher

import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread

/**
 * Интерфейс обработчика событий от сервиса пуш-уведомлений.
 *
 * @author am.boldinov
 */
interface PushMessagingHandler {

    /**
     * Обрабатывает пуш-сообщение от системного Messaging Service.
     */
    @WorkerThread
    fun onMessageReceived(message: Map<String, String>)

    /**
     * Обрабатывает получение токена от системного Messaging Service.
     */
    @AnyThread
    fun onNewToken(token: String)
}