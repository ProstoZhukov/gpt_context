package ru.tensor.sbis.pushnotification

import android.annotation.SuppressLint
import android.content.Context
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.SharedPreferencesUtils
import ru.tensor.sbis.verification_decl.login.event.AuthEvent
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.push_cloud_messaging.PushCloudMessaging
import ru.tensor.sbis.push_cloud_messaging.dispatcher.PushMessagingHandler
import ru.tensor.sbis.push_cloud_messaging.dispatcher.PushMessagingServiceRegistry
import ru.tensor.sbis.push_cloud_messaging.service.PushServiceSubscriber
import ru.tensor.sbis.mobile_services_decl.ApiAvailabilityService
import ru.tensor.sbis.mobile_services_decl.ServiceConnectionResult
import ru.tensor.sbis.pushnotification.center.PushCenter
import ru.tensor.sbis.pushnotification.service.KeepAliveForegroundService
import ru.tensor.sbis.pushnotification.util.PushLogger
import ru.tensor.sbis.pushnotification.util.counters.AppIconCounterUpdater
import ru.tensor.sbis.pushnotification_utils.R as RPushNotificationUtils

/**
 * Класс, управляющий подпиской пользователя на пуш-уведомления.
 * Автоматически подписывается на получение токена и отправляет его в сервис уведомлений.
 * Реагирует на события авторизации для очистки настроек и опубликованных данных по пушам.
 * Реагирует на события появления сети для повторного получения токена, в случае его изменения он будет отправлен
 * в облачный сервис уведомлений.
 *
 * @author ev.grigoreva
 */
class PushServiceSubscriptionManager(
    private val loginInterface: LoginInterface,
    private val networkUtils: NetworkUtils,
    private val pushCenter: PushCenter,
    private val context: Context,
    private val appIconCounterUpdater: AppIconCounterUpdater,
    private val pushServiceSubscriber: PushServiceSubscriber,
    private val apiAvailabilityService: ApiAvailabilityService,
    messagingServiceRegistry: PushMessagingServiceRegistry,
    messagingHandler: PushMessagingHandler
) {

    init {
        messagingServiceRegistry.registerHandler(messagingHandler)
        subscribe()
        subscribeOnAuthEvent()
        subscribeOnNetworkStateEvents()
    }

    @SuppressLint("CheckResult")
    private fun subscribeOnAuthEvent() {
        loginInterface.eventsObservable
            .subscribe { authEvent ->
                when (authEvent.eventType) {
                    AuthEvent.EventType.LOGIN -> {
                        if (authEvent.isNewAccount) {
                            clearSharedPrefsSettings()
                            // Очищаем все пуш-уведомления, которые могли остаться от предыдущего пользователя
                            pushCenter.cancelAll()
                        }
                        registerPushServices()
                    }
                    AuthEvent.EventType.LOGOUT -> {
                        if (authEvent.isSessionClosed) {
                            pushCenter.cancelAll()
                        }
                        appIconCounterUpdater.removeCounter()
                        KeepAliveForegroundService.disable(context)
                    }
                    AuthEvent.EventType.AUTHORIZED -> {
                        registerPushServices()
                    }
                    else                           -> {
                    }
                }
            }
    }

    private fun registerPushServices() {
        // Запускаем сервис стабилизации уведомлений, если нужно
        KeepAliveForegroundService.startIfNeed(context)
    }

    @SuppressLint("CheckResult")
    private fun subscribeOnNetworkStateEvents() {
        networkUtils
            .networkStateObservable()
            .skip(1) // Подписываемся только при изменении состояния подключения к сети. Начальное состояние не учитываем
            .subscribe { isConnected ->
                if (isConnected!!) {
                    subscribe()
                }
            }
    }

    /**
     * Подписывает пользователя на пуш уведомления
     */
    private fun subscribe() {
        pushServiceSubscriber.subscribe(context) {
            val servicesAvailable = apiAvailabilityService.checkServicesAvailability(context)
            if (servicesAvailable != ServiceConnectionResult.SUCCESS) {
                PushLogger.warning("Сервисы ${PushCloudMessaging.getServiceName()} устарели или недоступны, код ошибки: $servicesAvailable")
            } else {
                PushLogger.error(it)
            }
        }
    }

    private fun clearSharedPrefsSettings() {
        SharedPreferencesUtils.setBooleanValueFromResources(
            context,
            context.getString(RPushNotificationUtils.string.push_notification_utils_settings_notification_sound_key),
            R.bool.push_notification_default_sound_enabled
        )

        SharedPreferencesUtils.setBooleanValueFromResources(
            context,
            context.getString(RPushNotificationUtils.string.push_notification_utils_settings_notification_vibration_key),
            R.bool.push_notification_default_vibrate_enabled
        )
    }
}