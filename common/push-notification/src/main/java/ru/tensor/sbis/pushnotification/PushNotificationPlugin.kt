package ru.tensor.sbis.pushnotification

import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.verification_decl.permission.startup.StartupPermissionProvider
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.pushnotification.center.PushCenter
import ru.tensor.sbis.pushnotification.controller.notification.DigestNotificationController
import ru.tensor.sbis.pushnotification.di.PushNotificationComponent
import ru.tensor.sbis.pushnotification.di.PushNotificationComponentInitializer

/**
 * Плагин для пуш-уведомлений.
 *
 * @author kv.martyshenko
 */
object PushNotificationPlugin : BasePlugin<PushNotificationPlugin.CustomizationOptions>() {
    val notificationComponent: PushNotificationComponent by lazy {
        PushNotificationComponentInitializer(
            pushAppNameProvider.get().getAppName(),
            loginInterface.get(),
            mainActivityProvider.get()
        ).init(commonSingletonComponent.get())
    }

    private lateinit var commonSingletonComponent: FeatureProvider<CommonSingletonComponent>
    private lateinit var mainActivityProvider: FeatureProvider<MainActivityProvider>
    private lateinit var loginInterface: FeatureProvider<LoginInterface>
    private lateinit var pushAppNameProvider: FeatureProvider<PushAppNameProvider>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(PushCenter::class.java) { notificationComponent.getPushCenter() },
        FeatureWrapper(StartupPermissionProvider::class.java) { notificationComponent.getStartupPermissionProvider() }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(CommonSingletonComponent::class.java) { commonSingletonComponent = it }
        .require(MainActivityProvider::class.java) { mainActivityProvider = it }
        .require(LoginInterface::class.java) { loginInterface = it }
        .require(PushAppNameProvider::class.java) { pushAppNameProvider = it }
        .build()

    override val customizationOptions: CustomizationOptions = CustomizationOptions()

    override fun doAfterInitialize() {
        //Инициализация DI компонента
        notificationComponent.getPushCenter().apply {
            if (customizationOptions.digestMessageHandlerEnabled) {
                registerNotificationController(PushType.DIGEST, DigestNotificationController(application))
            }
        }
    }

    /**
     * Конфигурация плагина
     */
    class CustomizationOptions internal constructor() {

        /**
         * Активирован ли обработчик агрегированных пуш-уведомлений.
         * Кастомизация подписки на дайджест введена по причине отсутствия проверки в облаке
         * на соответствие типов уведомлений для конкретного приложения. (прооисходит проверка только по пользователю).
          */
        var digestMessageHandlerEnabled: Boolean = true
    }
}