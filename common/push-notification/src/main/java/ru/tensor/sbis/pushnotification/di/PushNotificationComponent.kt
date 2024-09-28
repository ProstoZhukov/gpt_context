package ru.tensor.sbis.pushnotification.di

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.verification_decl.permission.startup.StartupPermissionProvider
import ru.tensor.sbis.pushnotification.center.PushCenter
import ru.tensor.sbis.pushnotification.PushServiceSubscriptionManager
import ru.tensor.sbis.pushnotification.controller.base.helper.PushBuildingHelper
import ru.tensor.sbis.pushnotification.controller.notification.base.PushIntentHelper
import ru.tensor.sbis.pushnotification.proxy.NotificationManagerInterface
import ru.tensor.sbis.pushnotification.repository.PushNotificationRepository
import ru.tensor.sbis.mobile_services_decl.ApiAvailabilityService

/**
 * Основной компонент модуля push-уведомлений.
 *
 * @author am.boldinov
 */
@PushNotificationScope
@Component(
    dependencies = [
        CommonSingletonComponent::class
    ],
    modules = [
        PushNotificationModule::class
    ]
)
interface PushNotificationComponent {

    fun getPushCenter(): PushCenter

    fun getPushNotificationRepository(): PushNotificationRepository

    fun getPushServiceSubscriptionManager(): PushServiceSubscriptionManager

    fun getAvailabilityService(): ApiAvailabilityService

    fun getMainActivityProvider(): MainActivityProvider

    fun getPushBuildingHelper(): PushBuildingHelper

    fun getPushIntentHelper(): PushIntentHelper

    fun getNotificationManager(): NotificationManagerInterface

    fun getStartupPermissionProvider(): StartupPermissionProvider

    @Component.Builder
    interface Builder {

        fun commonSingletonComponent(component: CommonSingletonComponent): Builder

        fun pushModule(module: PushNotificationModule): Builder

        @BindsInstance
        fun mainActivityProvider(mainActivityProvider: MainActivityProvider): Builder

        fun build(): PushNotificationComponent

    }

}