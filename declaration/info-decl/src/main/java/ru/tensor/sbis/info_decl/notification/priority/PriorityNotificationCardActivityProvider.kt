package ru.tensor.sbis.info_decl.notification.priority

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс поставщика экрана карточки приоритетного уведомления
 */
interface PriorityNotificationCardActivityProvider : Feature {

    /**
     * Получить [Intent], открывающий Activity приоритетного уведомления
     * @param isCancellable - доступно ли закрытие экрана пользователем
     * @param event - модель события
     * @return Intent для открытия карточки приоритетного уведомления
     */
    fun getPriorityNotificationCardIntent(
        context: Context,
        isCancellable: Boolean,
        event: PriorityNotificationEvent
    ) : Intent
}