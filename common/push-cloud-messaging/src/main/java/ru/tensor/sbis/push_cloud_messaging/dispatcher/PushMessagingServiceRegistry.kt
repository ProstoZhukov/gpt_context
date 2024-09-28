package ru.tensor.sbis.push_cloud_messaging.dispatcher

/**
 * Реестр обработчиков событий от сервиса пуш-уведомлений.
 * Используется для хранения и делегирования им соответствующих вызовов.
 *
 * @author am.boldinov
 */
interface PushMessagingServiceRegistry {

    /**
     * Зарегистрировать обработчик событий на сервисе пуш-уведомлений.
     */
    fun registerHandler(handler: PushMessagingHandler)

    /**
     * Отменить регистрацию обработчика событий на сервисе пуш-уведомлений.
     */
    fun unregisterHandler(handler: PushMessagingHandler)
}