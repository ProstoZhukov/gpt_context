package ru.tensor.sbis.communicator.common.data.theme

import ru.tensor.sbis.info_decl.model.NotificationType
import ru.tensor.sbis.info_decl.notification.view.NotificationListViewConfiguration
import timber.log.Timber
import java.util.UUID

/**
 * Данные по уведомлениям в реестре диалогов.
 *
 * @property uuid идентификатор темы.
 * @property toolbarTitle текст заголовка для списка уведомлений по текущему типу.
 * @property photoUrl url фотографии для шапки.
 * @property noticeType тип уведомления.
 *
 * @author vv.chekurda
 */
data class ThemeNoticeData(
    val uuid: UUID,
    val toolbarTitle: String,
    val photoUrl: String,
    val noticeType: Int
) {
    private val notificationType = NotificationType.fromValue(noticeType)
        ?: NotificationType.UNKNOWN.also {
            Timber.e(IllegalStateException("ThemeFragment: NotificationType.UNKNOWN for theme $uuid"))
        }

    /**
     * Получить конфигурацию View экрана списка уведомлений по текущему типу.
     */
    val configuration: NotificationListViewConfiguration
        get() = NotificationListViewConfiguration(
            typesFilter = setOf(notificationType),
            isPaginationEnabled = true,
            isSwipeEnabled = true,
            needTopDividerForFirst = false
        )
}