package ru.tensor.sbis.pushnotification.di

import javax.inject.Scope

/**
 * Область видимости основного компонента модуля push-уведомлений.
 *
 * @author am.boldinov
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class PushNotificationScope