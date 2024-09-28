package ru.tensor.sbis.info_decl.notification.priority

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.info_decl.notification.NotificationUUID
import java.util.Date

/**
 * Событие для немедленного отображения приоритетного уведомления
 *
 * @author ev.grigoreva on 19.03.19.
 */
@Parcelize
data class PriorityNotificationEvent(
    val notificationUuid: NotificationUUID,
    val notificationDate: Date,
    val viewModel: String
) : Parcelable