package ru.tensor.sbis.pushnotification.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.lifecycle.AppLifecycleTracker
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.verification_decl.permission.startup.StartupPermissionProvider
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.push.generated.PushService
import ru.tensor.sbis.push_cloud_messaging.PushCloudMessaging
import ru.tensor.sbis.push_cloud_messaging.dispatcher.PushMessagingHandler
import ru.tensor.sbis.push_cloud_messaging.dispatcher.PushMessagingServiceRegistry
import ru.tensor.sbis.mobile_services_decl.ApiAvailabilityService
import ru.tensor.sbis.push_cloud_messaging.service.PushServiceSubscriber
import ru.tensor.sbis.pushnotification.PushServiceSubscriptionManager
import ru.tensor.sbis.pushnotification.center.PushCenter
import ru.tensor.sbis.pushnotification.controller.base.helper.PushBuildingHelper
import ru.tensor.sbis.pushnotification.controller.command.ConfirmPushCommand
import ru.tensor.sbis.pushnotification.controller.command.PushPostProcessCommand
import ru.tensor.sbis.pushnotification.controller.notification.base.PushIntentHelper
import ru.tensor.sbis.pushnotification.proxy.NotificationManagerInterface
import ru.tensor.sbis.pushnotification.proxy.TransactionNotificationManager
import ru.tensor.sbis.pushnotification.repository.*
import ru.tensor.sbis.pushnotification.service.PushMessagingDefaultHandler
import ru.tensor.sbis.pushnotification.util.PushStartupPermissionProvider
import ru.tensor.sbis.pushnotification.util.counters.AppIconCounterUpdater
import ru.tensor.sbis.pushnotification.util.counters.NotificationBadge

/**
 * Модуль основного компонента push-уведомлений.
 *
 * @author am.boldinov
 */
@Module
class PushNotificationModule(
    private val appName: String,
    private val loginInterface: LoginInterface
) {

    @Provides
    @PushNotificationScope
    internal fun providePushNotificationService(): DependencyProvider<PushService> {
        return DependencyProvider.create { return@create PushService.instance() }
    }

    @Provides
    @PushNotificationScope
    internal fun providePushCenter(
        context: Context,
        manager: TransactionNotificationManager,
        appIconCounterUpdater: AppIconCounterUpdater,
        repository: PushNotificationRepository,
        commands: Array<PushPostProcessCommand>
    ): PushCenter {
        return PushCenter(context, manager, repository, appIconCounterUpdater, *commands)
    }

    @Provides
    @PushNotificationScope
    internal fun provideCommands(
        context: Context,
        repository: PushNotificationRepository
    ): Array<PushPostProcessCommand> {
        return arrayOf(ConfirmPushCommand(context, repository))
    }

    @Provides
    @PushNotificationScope
    internal fun provideTransactionNotificationManager(context: Context) =
        TransactionNotificationManager(context, loginInterface)

    @Provides
    @PushNotificationScope
    internal fun provideNotificationManager(manager: TransactionNotificationManager): NotificationManagerInterface {
        return manager
    }

    @Provides
    @PushNotificationScope
    internal fun provideDataConverter() = PushNotificationMessageConverter()

    @Provides
    @PushNotificationScope
    internal fun provideRepository(
        pushService: DependencyProvider<PushService>,
        converter: PushNotificationMessageConverter
    ): PushNotificationRepository {
        return PushNotificationServiceRepositoryImpl(appName, pushService, converter)
    }

    @Provides
    @PushNotificationScope
    internal fun providePushServiceSubscriptionManager(
        networkUtils: NetworkUtils,
        pushCenter: PushCenter,
        context: Context,
        appIconCounterUpdater: AppIconCounterUpdater,
        pushServiceSubscriber: PushServiceSubscriber,
        apiAvailabilityService: ApiAvailabilityService,
        messagingServiceRegistry: PushMessagingServiceRegistry,
        messagingHandler: PushMessagingHandler
    ): PushServiceSubscriptionManager {
        return PushServiceSubscriptionManager(
            loginInterface,
            networkUtils,
            pushCenter,
            context,
            appIconCounterUpdater,
            pushServiceSubscriber,
            apiAvailabilityService,
            messagingServiceRegistry,
            messagingHandler
        )
    }

    @Provides
    @PushNotificationScope
    internal fun provideMessagingHandler(
        pushCenter: PushCenter,
        repository: PushNotificationRepository
    ): PushMessagingHandler {
        return PushMessagingDefaultHandler(pushCenter, repository)
    }

    @Provides
    @PushNotificationScope
    internal fun providePushServiceSubscriber(): PushServiceSubscriber {
        return PushCloudMessaging.getServiceSubscriber()
    }

    @Provides
    @PushNotificationScope
    internal fun provideApiAvailabilityService(): ApiAvailabilityService {
        return PushCloudMessaging.getAvailabilityService()
    }

    @Provides
    @PushNotificationScope
    internal fun provideMessagingServiceRegistry(): PushMessagingServiceRegistry {
        return PushCloudMessaging.getMessagingRegistry()
    }

    @Provides
    @PushNotificationScope
    internal fun provideBuildingHelper(context: Context): PushBuildingHelper {
        return PushBuildingHelper(context)
    }

    @Provides
    @PushNotificationScope
    internal fun providePushIntentHelper(context: Context): PushIntentHelper {
        return PushIntentHelper(context)
    }

    @Provides
    @PushNotificationScope
    internal fun provideNotificationBadge(context: Context): NotificationBadge = NotificationBadge(context)

    @Provides
    @PushNotificationScope
    internal fun provideAppIconCounterUpdater(
        notificationBadge: NotificationBadge,
        lifecycleTracker: AppLifecycleTracker
    ): AppIconCounterUpdater {
        return AppIconCounterUpdater(notificationBadge, lifecycleTracker)
    }

    @Provides
    @PushNotificationScope
    internal fun providePushPermissionProvider(): StartupPermissionProvider {
        return PushStartupPermissionProvider()
    }
}