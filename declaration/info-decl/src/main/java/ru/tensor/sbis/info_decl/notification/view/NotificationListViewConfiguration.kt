package ru.tensor.sbis.info_decl.notification.view

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.info_decl.model.NotificationType
import java.io.Serializable

/**
 * Конфигурация отображения и загрузки View со списком уведомлений.
 * @param [typesFilter] типы уведомлений, по которым проводится фильтрация. При [emptySet] фильтрация не производится
 * @param [isPaginationEnabled] включена ли постраничная загрузка. При false отображается список целиком
 * @param [isSwipeEnabled] Доступна ли возможность вызвать меню уведомления по свайпу элемента влево
 * @param [needTopDividerForFirst] Нужен ли отступ сверху для списка
 * @param [asFullList] Режим отображения вью (как блок для встраивания в другой список или как полноценный список).
 * Для режима полноценного списка будет отображаться заглушка при отсутствии контента и прогресс загрузки.
 *
 * @author am.boldinov
 */
@Parcelize
data class NotificationListViewConfiguration(
    val typesFilter: Set<NotificationType>,
    val isPaginationEnabled: Boolean,
    val isSwipeEnabled: Boolean,
    val needTopDividerForFirst: Boolean = true,
    val asFullList: Boolean = false
): Serializable, Parcelable {

    companion object {

        @JvmStatic
        fun defaultConfiguration(): NotificationListViewConfiguration {
            return NotificationListViewConfiguration(
                emptySet(),
                isPaginationEnabled = true,
                isSwipeEnabled = true
            )
        }

        @JvmStatic
        fun viewHolderConfiguration(
            typesFilter: Set<NotificationType>
        ): NotificationListViewConfiguration {
            return NotificationListViewConfiguration(
                typesFilter,
                isPaginationEnabled = false,
                isSwipeEnabled = false
            )
        }
    }
}