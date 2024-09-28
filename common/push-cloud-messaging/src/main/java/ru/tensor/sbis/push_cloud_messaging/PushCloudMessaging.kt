package ru.tensor.sbis.push_cloud_messaging

import ru.tensor.sbis.push_cloud_messaging.dispatcher.PushMessagingHandlerDispatcher
import ru.tensor.sbis.push_cloud_messaging.dispatcher.PushMessagingServiceProxy
import ru.tensor.sbis.push_cloud_messaging.dispatcher.PushMessagingServiceRegistry
import ru.tensor.sbis.push_cloud_messaging.service.ApiAvailabilityServiceImpl
import ru.tensor.sbis.push_cloud_messaging.service.PushServiceSubscriber
import ru.tensor.sbis.push_cloud_messaging.service.PushServiceSubscriberImpl
import ru.tensor.sbis.mobile_services_decl.ApiAvailabilityService

/**
 * Представляет собой инструменты для подписки на пуш-уведомления - обновления токена для регистрации на сервере и
 * получения новых пуш-сообщений.
 * @see PushServiceSubscriber
 * @see PushMessagingServiceRegistry
 * @see ApiAvailabilityService
 *
 * @author am.boldinov
 */
object PushCloudMessaging {

    private val subscriber = PushServiceSubscriberImpl()
    private val availabilityService = ApiAvailabilityServiceImpl()
    private val handlerDispatcher = PushMessagingHandlerDispatcher()

    /**
     * Подписчик для получения новых пуш-уведомлений.
     * Необходимо использовать на старте приложения для обновления токена.
     */
    fun getServiceSubscriber(): PushServiceSubscriber {
        return subscriber
    }

    /**
     * Сервис проверки доступности на устройстве мобильных сервисов.
     */
    fun getAvailabilityService(): ApiAvailabilityService {
        return availabilityService
    }

    /**
     * Реестр обработчиков событий от сервиса пуш-уведомлений.
     * Необходимо использовать для регистрации обработчиков обновления токена и получения новых событий.
     */
    fun getMessagingRegistry(): PushMessagingServiceRegistry {
        return handlerDispatcher
    }

    /**
     * Название текущего сервиса уведомлений, который включен в сборку.
     * Необходимо использовать для отправки на сервер вместе с токеном.
     */
    fun getServiceName(): String {
        return BuildConfig.PUSH_SERVICE_NAME
    }

    internal fun getMessagingProxy(): PushMessagingServiceProxy {
        return handlerDispatcher
    }
}