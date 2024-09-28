package ru.tensor.sbis.info_decl.notification

import ru.tensor.sbis.info_decl.model.NotificationType
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.toolbox_decl.counters.CounterProvider
import ru.tensor.sbis.toolbox_decl.counters.UnreadAndTotalCounterModel

/**
 * Создаёт [CounterProvider] со счётчиками для указанных типов уведомлений
 *
 * @author us.bessonov
 */
interface NotificationCounterProviderFactory : Feature {

    fun createNotificationCounterProvider(typesFilter: Set<NotificationType>): CounterProvider<UnreadAndTotalCounterModel>
}

/**
 * Предоставляет [NotificationCounterProviderFactory]
 *
 * @author us.bessonov
 */
interface NotificationCounterProviderFactoryProvider {

    val notificationCounterProviderFactory: NotificationCounterProviderFactory
}