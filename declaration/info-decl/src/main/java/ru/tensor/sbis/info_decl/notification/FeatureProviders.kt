package ru.tensor.sbis.info_decl.notification

import android.content.Intent
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.theme.res.SbisString
import ru.tensor.sbis.info_decl.model.NotificationType
import ru.tensor.sbis.info_decl.notification.view.NotificationListViewConfiguration
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.io.Serializable
import java.util.UUID

/**
 * Интерфейс поставщика корневого фрагмента реестра уведомлений
 *
 * @author s.r.golovkin
 */
interface NotificationListFragmentProvider : Feature {

    /**
     * Получить фрагмент списка уведомлений
     * @return фрагмент реестра уведомлений
     */
    fun getNotificationListFragment(
        configuration: NotificationListFragmentConfiguration = NotificationListFragmentConfiguration()
    ): Fragment

    /**
     * Получить фрагмент списка уведомлений в виде карточки
     * @return фрагмент списка уведомлений в соответствии с заданной конфигурацией
     */
    fun getNotificationListFragmentAsCard(
        configuration: NotificationListFragmentAsCardConfiguration? = null
    ): Fragment
}

/**
 * Конфигурация отображения и загрузки фрагмента со списком уведомлений.
 * @param title модель строки заголовка реестра (в тулбаре).
 * @param navigateBack доступна ли навигация по стрелке в шапке.
 */
data class NotificationListFragmentConfiguration(
    val title: SbisString? = null,
    val navigateBack: Boolean = false
) : Serializable

/**
 * Конфигурация отображения и загрузки фрагмента со списком уведомлений в виде карточки.
 * @param title строка заголовка в шапке карточки.
 * @param imageUrl ссылка на изображение в шапке карточки.
 * @param listConfig конфигурация списка.
 * @param navigateBack доступна ли навигация по стрелке в шапке.
 * @param swipeBackEnabled включен ли свайпбэк.
 */
@Parcelize
data class NotificationListFragmentAsCardConfiguration(
    val title: String? = null,
    val imageUrl: String? = null,
    val listConfig: NotificationListViewConfiguration? = null,
    val navigateBack: Boolean = false,
    val swipeBackEnabled: Boolean = false
) : Parcelable

/**
 * Интерфейс поставщика экрана простой карточки уведомления
 */
interface NotificationCardActivityProvider : Feature {

    companion object {
        const val NOTIFICATION_UUID_EXTRA_KEY = "base_notification_uuid"
    }

    /**
     * Получить [Intent], открывающий Activity простой карточки уведомления
     * @param notificationUuid - идентификатор уведомления
     * @param documentUuid - идентификатор документа
     * @param notificationType - тип уведомления
     * @return Intent для открытия простой карточки уведомления
     */
    fun getNotificationCardActivityIntent(
        notificationUuid: NotificationUUID,
        documentUuid: String?,
        notificationType: NotificationType
    ): Intent

    /**
     * Получить фрагмент простой карточки уведомления
     * @param notificationUuid - идентификатор уведомления
     * @param documentUuid - идентификатор документа
     * @param notificationType - тип уведомления
     * @return Фрагмент простой карточки уведомления
     */
    fun getNotificationCardFragment(
        notificationUuid: NotificationUUID,
        documentUuid: String?,
        notificationType: NotificationType
    ): Fragment
}

/**
 * Интерфейс для открытия экрана истории уведомлений.
 *
 * @author ev.grigoreva
 */
interface PushHistoryActivityProvider : Feature {
    /**
     * Получить [Intent], открывающий Activity истории уведомлений
     * @return Intent для открытия истории уведомлений
     */
    fun getPushHistoryIntent(): Intent
}

/**
 * Интерфейс для получения фрагмента с историей уведомлений.
 */
interface PushHistoryFragmentProvider : Feature {
    /**
     * Получить [Fragment] с историей уведомлений
     */
    fun getPushHistoryFragment(withNavigation: Boolean = true): Fragment
}

/**
 * Интерфейс для открытия экрана карточки контрагента
 */
interface ContractorCardActivityProvider : Feature {
    /**
     * Получить [Intent], открывающий Activity карточки контрагента
     * @param notificationUuid - идентификатор уведомления
     * используется для загрузки базовой карточки в случае отсутствия карточки контрагента по типу
     * @param documentUuid - идентификатор документа
     * @return Intent для открытия карточки контрагента
     */
    fun getContractorCardActivityIntent(
        notificationUuid: NotificationUUID?,
        documentUuid: NotificationUUID
    ): Intent
}

/**
 * Интерфейс для открытия экрана карточки торга
 */
interface TenderCardActivityProvider : Feature {

    /**
     * Получить [Intent], открывающий Activity карточки торга
     * @param notificationUuid - идентификатор уведомления
     * используется для загрузки базовой карточки в случае отсутствия карточки торга по типу
     * @param documentId - идентификатор документа
     * @param eventTypeValue - числовое значение типа события, по которому открыли карточку [NotificationType.value]
     * @param syncTypeValue - числовое значение подтипа уведомления
     * @return Intent для открытия карточки торга
     */
    fun getTenderCardActivityIntent(
        notificationUuid: NotificationUUID?,
        documentId: String,
        eventTypeValue: Int,
        syncTypeValue: Int
    ): Intent
}

/**
 * Интерфейс для открытия экрана карточки результатов проверки сотрудника
 */
interface ProblemEmployeeCardActivityProvider : Feature {

    /**
     * Получить [Intent], открывающий Activity карточки результатов проверки сотрудника
     * @param notificationUuid - идентификатор уведомления
     */
    fun getProblemEmployeeCardActivityIntent(notificationUuid: NotificationUUID): Intent
}

/**
 * Интерфейс для открытия экрана карточки налогов и штрафов
 */
interface TaxesPenaltiesActivityProvider : Feature {

    /**
     * Получить [Intent], открывающий Activity карточки налогов и штрафов
     * @param notificationUuid - идентификатор уведомления
     */
    fun getTaxesPenaltiesActivityIntent(notificationUuid: NotificationUUID): Intent
}

/**
 * Уникальный идентификатор для работы с уведомлениями
 *
 * @param uuid идентификатор уведомления
 * @param syncType подтип уведомления
 *
 * @author ev.grigoreva
 */
data class NotificationUUID(val uuid: UUID, val syncType: Int) : Serializable {

    fun getStringUuid(): String = uuid.toString()

    override fun toString(): String {
        return uuid.toString() + "_" + syncType
    }

    companion object {

        @JvmStatic
        fun fromStringValues(uuidString: String?, syncTypeStr: String?): NotificationUUID? {
            val uuid: UUID? = try {
                if (!uuidString.isNullOrEmpty()) UUID.fromString(uuidString) else null
            } catch (exception: IllegalArgumentException) {
                Log.e(
                    "NotificationUUID",
                    "Невозможно идентифицировать уведомление, некорректный uuid = $uuidString!",
                )
                null
            }
            val subType: Int? = try {
                if (!syncTypeStr.isNullOrEmpty()) Integer.valueOf(syncTypeStr) else null
            } catch (exception: NumberFormatException) {
                Log.e(
                    "NotificationUUID",
                    "Невозможно идентифицировать уведомление с uuid = $uuidString, некорректный тип = $syncTypeStr!",
                )
                null
            }
            return if (uuid != null && subType != null) {
                NotificationUUID(uuid, subType)
            } else null
        }

        /**
         * Преобразовывает строку вида "uuid_syncType" в модель идентификатора.
         * @see [NotificationUUID.toString]
         */
        @JvmStatic
        fun fromString(str: String): NotificationUUID? {
            return str.split("_", limit = 2).let {
                fromStringValues(it.getOrNull(0), it.getOrNull(1))
            }
        }

    }
}