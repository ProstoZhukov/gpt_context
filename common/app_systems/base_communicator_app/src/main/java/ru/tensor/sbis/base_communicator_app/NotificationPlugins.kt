package ru.tensor.sbis.base_communicator_app

import ru.tensor.sbis.notification_api.configuration.NotificationServiceConfiguration
import ru.tensor.sbis.notification_push.NotificationPushPlugin
import ru.tensor.sbis.notification_service.NotificationServicePlugin
import ru.tensor.sbis.notification_ui.NotificationPlugin
import ru.tensor.sbis.plugin_struct.BasePlugin

/** Плагины уведомлений, включающие реализацию реестра, карточек и пушей на основе контроллера уведомлений */
object NotificationPlugins {

    private val notificationServicePlugin: NotificationServicePlugin = NotificationServicePlugin
    private val notificationUiPlugin: NotificationPlugin = NotificationPlugin
    private val notificationPushPlugin: NotificationPushPlugin = NotificationPushPlugin.apply {
        customizationOptions.pushMessageHandlerEnabled = true
    }

    internal val plugins by lazy {
        arrayOf<BasePlugin<*>>(notificationUiPlugin, notificationServicePlugin, notificationPushPlugin)
    }

    fun setPushConfig(applyConfig: NotificationPushPlugin.CustomizationOptions.() -> Unit) {
        notificationPushPlugin.customizationOptions.applyConfig()
    }

    fun setServiceConfiguration(config: NotificationServiceConfiguration) {
        notificationServicePlugin.customizationOptions.apply { notificationServiceConfiguration = config }
        notificationUiPlugin.customizationOptions.apply { notificationServiceConfiguration = config }
        notificationPushPlugin.customizationOptions.apply { notificationServiceConfiguration = config }
    }
}