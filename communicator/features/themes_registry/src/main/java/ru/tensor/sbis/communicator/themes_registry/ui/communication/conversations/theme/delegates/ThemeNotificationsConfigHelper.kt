package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.delegates

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.communicator.generated.ThemeController
import ru.tensor.sbis.info_decl.model.NotificationFilterStrategy
import ru.tensor.sbis.info_decl.notification.NotificationFilterStrategyProvider
import timber.log.Timber

/**
 * Вспомогательная реализация для настройки доступных уведомлений в реестре диалогов.
 *
 * @author vv.chekurda
 */
internal object ThemeNotificationsConfigHelper {

    /**
     * Обновить доступные типы уведомлений для реестра диалогов.
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun updateAvailableNoticeTypes(filterStrategyProvider: NotificationFilterStrategyProvider?) {
        val strategy = filterStrategyProvider?.getNotificationFilterStrategy() ?:
            NotificationFilterStrategy.IncludeFilterStrategy()
        GlobalScope.launch {
            configureAvailableNoticeTypes(strategy)
        }
    }

    private suspend fun configureAvailableNoticeTypes(strategy: NotificationFilterStrategy) =
        withContext(Dispatchers.IO) {
            val controller = ThemeController.instance()
            val configure = when (strategy) {
                is NotificationFilterStrategy.ExcludeFilterStrategy -> controller::setExcludeNotificationsType
                is NotificationFilterStrategy.IncludeFilterStrategy -> controller::setIncludeNotificationsType
            }
            try {
                configure(strategy.getNotificationTypes())
                Timber.i("ThemeNotifications: configureAvailableNoticeTypes success")
            } catch (ex: Exception) {
                Timber.e(ex, "ThemeNotifications: configureAvailableNoticeTypes error")
            }
        }
}