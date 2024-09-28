package ru.tensor.sbis.pushnotification.center

import android.content.Context
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.R
import ru.tensor.sbis.pushnotification.controller.PushActionController
import ru.tensor.sbis.pushnotification.controller.PushNotificationController
import ru.tensor.sbis.pushnotification.controller.command.PushPostProcessCommand
import ru.tensor.sbis.pushnotification.proxy.TransactionNotificationManager
import ru.tensor.sbis.pushnotification.repository.PushNotificationRepository
import ru.tensor.sbis.pushnotification.util.PushDebug
import ru.tensor.sbis.pushnotification.util.counters.AppIconCounterUpdater
import ru.tensor.sbis.pushnotification_utils.notification.channels.NotificationChannelUtils

/**
 * Представляет собой точку входа для обработки событий по пуш-уведомлениям.
 * Получает новые события от Messaging Service, обрабатывает, объединяет частые сообщения в один список и
 * передает управление готовыми данными по пушам прикладным обработчикам, которые хранит на всё время жизни компонента.
 * Получает события о смахивании пуш-уведомления пользователем.
 * Регистрирует основные каналы уведомлений для публикации.
 *
 * @author am.boldinov
 */
class PushCenter private constructor(
    context: Context,
    repository: PushNotificationRepository,
    appIconCounterUpdater: AppIconCounterUpdater,
    private val pushHandlerDelegate: PushHandlingManagerImpl
) : PushSubscriptionManager by PushSubscriptionManagerImpl(repository),
    PushHandlingManager by pushHandlerDelegate,
    Feature {

    internal constructor(
        context: Context,
        notificationManager: TransactionNotificationManager,
        repository: PushNotificationRepository,
        appIconCounterUpdater: AppIconCounterUpdater,
        vararg postProcessCommands: PushPostProcessCommand
    ) : this(
        context,
        repository,
        appIconCounterUpdater,
        PushHandlingManagerImpl(notificationManager, repository, *postProcessCommands)
    )

    init {
        initNotificationChannels(context)
        PushDebug.enable(this)
        registerPushHandler(
            PushCounterHandler(appIconCounterUpdater) {
                pushHandlerDelegate.getNotificationController(it.type) != null
            }
        )
    }

    /**
     * Регистрирует прикладной обработчик пуш-уведомления.
     * Инициирует подписку на пуши по переданному типу для поступлениях их на устройство.
     *
     * @see PushHandlingManager.registerNotificationController
     */
    override fun registerNotificationController(type: PushType, controller: PushNotificationController) {
        pushHandlerDelegate.registerNotificationController(type, controller)
        addSubscriber(type, controller)
    }

    /**
     * Удаляет прикладной обработчик пуш-уведомления и отменяет подписку на указанный тип уведомления.
     * Уведомления с переданным типом больше не будут поступать на устройство (в случае если не зарегистрирован
     * [registerActionController] - дополнительный обработчик действий)
     */
    override fun unregisterNotificationController(type: PushType) {
        pushHandlerDelegate.getNotificationController(type)?.let { controller ->
            removeSubscriber(type, controller)
        }
        pushHandlerDelegate.unregisterNotificationController(type)
    }

    /**
     * Регистрирует прикладной обработчик пуш-уведмления.
     * Инициирует подписку на пуши по переданному типу для поступлениях их на устройство.
     *
     * @see PushHandlingManager.registerActionController
     */
    override fun registerActionController(type: PushType, controller: PushActionController) {
        pushHandlerDelegate.registerActionController(type, controller)
        addSubscriber(type, controller)
    }

    /**
     * Удаляет прикладной обработчик действий над пуш-уведомлением и отменяет подписку на указанный тип уведомления.
     * Уведомления с переданным типом больше не будут поступать на устройство (в случае если не зарегистрирован
     * [registerNotificationController] - дополнительный обработчик для показа уведомлений)
     */
    override fun unregisterActionController(type: PushType) {
        pushHandlerDelegate.getActionController(type)?.let { controller ->
            removeSubscriber(type, controller)
        }
        pushHandlerDelegate.unregisterActionController(type)
    }

    /**
     * Устанавливает каналы уведомлений, в которые будет происходить публикация
     */
    private fun initNotificationChannels(context: Context) {
        NotificationChannelUtils.submitDefaultChannel(
            context,
            context.getString(R.string.push_notification_main_channel_name)
        )
    }
}