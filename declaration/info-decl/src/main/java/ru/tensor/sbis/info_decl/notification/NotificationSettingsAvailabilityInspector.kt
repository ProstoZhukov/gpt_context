package ru.tensor.sbis.info_decl.notification

import androidx.annotation.WorkerThread
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс, предназначенный для проверки включены ли уведомления
 *
 * @author sr.golovkin
 */
interface NotificationSettingsAvailabilityInspector : Feature {

    /**
     * Проверить, включены ли уведомления
     * @return true - включены.
     */
    @WorkerThread
    fun isEnabled(): Boolean
}