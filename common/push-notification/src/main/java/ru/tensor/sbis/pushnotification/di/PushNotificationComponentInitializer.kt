package ru.tensor.sbis.pushnotification.di

import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.android_ext_decl.MainActivityProvider
import ru.tensor.sbis.verification_decl.login.LoginInterface

/**
 * Класс-инициализатор основного компонента модуля push-уведомлений.
 *
 * @author am.boldinov
 */
class PushNotificationComponentInitializer(
    private val appName: String,
    private val loginInterface: LoginInterface,
    private val mainActivityProvider: MainActivityProvider
) {

    /**
     * Метод, инициализирующий основной компонент модуля push-уведомлений.
     *
     * @param commonSingletonComponent компонент common модуля приложения
     */
    fun init(commonSingletonComponent: CommonSingletonComponent): PushNotificationComponent {
        val component = DaggerPushNotificationComponent.builder()
            .commonSingletonComponent(commonSingletonComponent)
            .pushModule(PushNotificationModule(appName, loginInterface))
            .mainActivityProvider(mainActivityProvider)
            .build()
        component.getPushServiceSubscriptionManager() // force init
        return component
    }
}