package ru.tensor.sbis.pushnotification.di

import android.content.Context
import ru.tensor.sbis.pushnotification.PushNotificationPlugin

/**
 * Поставщик основного компонента модуля push-уведомлений.
 *
 * @author am.boldinov
 */
object PushNotificationComponentProvider {
    /**
     * @return основной компонент модуля push-уведомлений
     */
    @JvmStatic
    fun get(context: Context): PushNotificationComponent {
        /* ComponentProvider оставляем, в будущем может быть полезным для подмены реализации. */
        return PushNotificationPlugin.notificationComponent
    }
}