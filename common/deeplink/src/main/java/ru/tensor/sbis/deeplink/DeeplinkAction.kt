package ru.tensor.sbis.deeplink

import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.util.UUID

/**
 * Представляет действие, которое необходимо выполнить при открытии внешней ссылки
 */
sealed interface DeeplinkAction

/**
 * Представляет действие [DeeplinkAction], которое может быть сериализовано.
 */
interface SerializableDeeplinkAction : DeeplinkAction, Serializable

/**
 * Представляет действие [DeeplinkAction], которое одновременно является [Parcelable].
 */
interface ParcelableDeeplinkAction : DeeplinkAction, Parcelable

/**
 * Нет действия
 */
internal object NoDeeplinkAction : SerializableDeeplinkAction {

    // TODO проверить другие возможные решения https://online.sbis.ru/opendoc.html?guid=60ccb439-3a6b-4b76-a15a-0b61c2d4db97
    /** Для решения проблемы с множественной десериализацией object в kotlin */
    private fun readResolve(): Any = NoDeeplinkAction
}

/**
 * Представляет действие открытия сущности по идентификатору
 */
data class OpenEntityDeeplinkAction constructor(val uuid: String) : SerializableDeeplinkAction

/**
 * Представляет действия открытия сущности по uri
 */
data class OpenUriDeeplinkAction(val uri: String) : SerializableDeeplinkAction

/**
 * Представляет действие модуля задач
 */
sealed class TaskDeeplinkAction : SerializableDeeplinkAction

/**
 * Открывает вкладку в модуле задач.
 * @property isOnMe true если нужно окрыть "на мне", false - "от меня".
 */
class OpenTabTaskDeeplinkAction(
    val isOnMe: Boolean
) : TaskDeeplinkAction()

/**
 * Представляет действие открытия задачи модуля задач
 */
sealed class OpenTaskDeeplinkAction : TaskDeeplinkAction()

/**
 * Представляет действие создания задачи
 */
sealed class CreateNewTaskDeeplinkAction : TaskDeeplinkAction()

/**
 * Представляет действие открытия карточки документа по uuid.
 * @property uuid глобальный идентификатор задачи.
 * @property eventUuid идентификатор активного события, либо null если неизвестен.
 * @property docType тип документа для подбора правильной карточки при открытии.
 * @property isFromPush true если отправлено из пуша, иначе false.
 */
data class OpenTaskByUuidDeeplinkAction(
    val uuid: String,
    val eventUuid: String?,
    val docType: String?,
    val isFromPush: Boolean = false
): OpenTaskDeeplinkAction()

/**
 * Представляет действие открытия карточки опубликованной инструкции в реестре задач.
 * @property uuid идентификатор инструкции.
 * @property docType тип документа для подбора правильной карточки при открытии.
 * @property isFromPush true если отправлено из пуша, иначе false.
 */
data class OpenInstructionTaskDeeplinkAction(
    val uuid: UUID,
    val docType: String,
    val isFromPush: Boolean = false
): OpenTaskDeeplinkAction()

/**
 * Представляет действие открытия карточки документа по uuid.
 * @property cloudId облачный идентификатор задачи.
 * @property docType тип документа для подбора правильной карточки при открытии.
 */
data class OpenTaskByCloudIdDeeplinkAction(
    val cloudId: Long,
    val docType: String?,
): OpenTaskDeeplinkAction()

/**
 * Представляет действие создания задачи c вложениями
 * @property attachments вложения, которые нужно сразу прикрепить
 */
data class CreateNewTaskWithAttachmentsDeeplinkAction(val attachments: List<String>) : CreateNewTaskDeeplinkAction()

/**
 * Представляет действие создания задачи c описанием
 * @property description описание, которые нужно сразу отобразить
 */
data class CreateNewTaskWithDescriptionDeeplinkAction(val description: String) : CreateNewTaskDeeplinkAction()

/**
 * Представляет действие модуля коммуникатора
 */
sealed interface CommunicatorDeeplinkAction : DeeplinkAction

/**
 * Представляет действие открытия переписки
 * @property dialogUuid          идентификатор диалога
 * @property messageUuid         идентификатор сообщения
 * @property recipients          получатели, которых необходимо подставить
 * @property isChat              true, если переписка - чат
 * @property title               заголовок переписки для оффлайна
 * @property photoId             идентификатор аватарки переписки для оффлайна
 * @property isGroupConversation true, если это групповая переписка > 2 участников.
 */
@Parcelize
data class OpenConversationDeeplinkAction(
    val dialogUuid: UUID? = null,
    val messageUuid: UUID? = null,
    val recipients: ArrayList<UUID>? = null,
    val isChat: Boolean = false,
    val title: String? = null,
    val photoId: String? = null,
    val isGroupConversation: Boolean = false,
    val filesToShare: ArrayList<Uri>? = null
) : CommunicatorDeeplinkAction, ParcelableDeeplinkAction

/**
 * Представляет действие переключения вкладки диалоги/чаты в реестре
 * @property isChatTab true, если необходимо переключиться на вкладку чатов
 */
data class SwitchThemeTabDeeplinkAction(val isChatTab: Boolean) : CommunicatorDeeplinkAction, SerializableDeeplinkAction

/**
 * Шаринг контента в реестр сообщений
 */
@Parcelize
class ShareToMessagesDeeplinkAction(val dataIntent: Intent) : CommunicatorDeeplinkAction, ParcelableDeeplinkAction

/**
 * Представляет действие в разделе уведомлений
 */
sealed class NotificationsDeeplinkAction : SerializableDeeplinkAction

/**
 * Открытие нарушения в разделе уведомлений
 */
data class OpenViolationDeeplinkAction(
    val uuid: UUID,
    val violationNotificationUUID: UUID?
) : NotificationsDeeplinkAction()

/**
 * Открывает вкладку календаря
 * @property calendarMode режим открытия календаря
 * @property openingViewType тип открываемого экрана календаря
 */
data class OpenCalendarScreenDeeplinkAction(
    val calendarMode: Int,
    val openingViewType: Int
) : SerializableDeeplinkAction

/**
 * Открытие карточки отпуска
 */
data class OpenEventDocScreenDeeplinkAction(
    val dateEnd: String?,
    val dateStart: String?,
    val docId: String?,
    val employeeId: String?,
    val note: String?,
    val personId: String?,
    val type: String?,
    val vacationId: String?,
    val workID: String?,
    val workUUID: String?
) : SerializableDeeplinkAction

/**
 * Открытие инструкции в разделе уведомлений
 */
data class OpenInstructionDeeplinkAction(
    val uuid: UUID,
    val title: String,
    val url: String
) : NotificationsDeeplinkAction()

/**
 * Открытие профиля сотрудника
 */
data class OpenProfileDeeplinkAction(
    val dialogUuid: UUID? = null,
    val messageUuid: UUID? = null,
    val profileUuid: UUID,
) : NotificationsDeeplinkAction()

/**
 * Открытие документа в webView
 */
data class OpenWebViewDeeplinkAction(
    val dialogUuid: UUID? = null,
    val messageUuid: UUID? = null,
    val documentTitle: String,
    val documentUrl: String
) : NotificationsDeeplinkAction()

/**
 * Открытие карточки обсуждения статьи
 *
 * @param documentUuid  идентификатор документа
 * @param dialogUuid    идентификатор диалога
 * @param messageUuid   идентификатор сообщения
 * @param documentUrl   url документа (опционально, для открытия в веб-вью)
 * @param documentTitle заголовок документа (опционально, для открытия в веб-вью)
 * @param isSocnetEvent true, если релевантное сообщение - событие соц.сети (вас упомянули и тд.)
 */
data class OpenArticleDiscussionDeeplinkAction(
    val documentUuid: UUID,
    val dialogUuid: UUID,
    val messageUuid: UUID?,
    val documentUrl: String? = null,
    val documentTitle: String? = null,
    val isSocnetEvent: Boolean = false
) : NotificationsDeeplinkAction()

/**
 * Обработать клик на пуш уведомление для типов [noticeTypes].
 * Несколько значений может прийти для сгруппированного пуша. Если все пуши в группе одного типа -
 * в set будет одно значение.
 * Может являться расширенеием для нескольких уровней обработки, например, список + карточка конкретного уведомления.
 */
open class HandlePushNotificationDeeplinkAction(
    val noticeTypes: Set<Int>
) : NotificationsDeeplinkAction()

/**
 * Открытие событий в разделе уведомлений
 */
sealed class OpenEventCardDeeplinkAction : SerializableDeeplinkAction {
    abstract val docUuid: String
    abstract val notificationUuid: String?
}

/**
 * Открытие совещения в разделе уведомлений
 */
data class OpenMeetingDeeplinkAction(
    override val docUuid: String,
    override val notificationUuid: String? = null
) : OpenEventCardDeeplinkAction()

/**
 * Открытие вебинара в разделе уведомлений
 */
data class OpenWebinarDeeplinkAction(
    override val docUuid: String,
    override val notificationUuid: String? = null,
    val showTranslation: Boolean = false
) : OpenEventCardDeeplinkAction()

/**
 * Открытие карточки новостей
 * @property[commentUuid] uuid комментария, если была передана ссылка на комментарий
 */
data class OpenNewsDeepLinkAction(
    val docUuid: String,
    val commentUuid: UUID? = null
) : SerializableDeeplinkAction

/**
 * Открытие базы знаний
 */
data class OpenKnowledgeBaseDeepLinkAction(
    val baseUuid: UUID
) : SerializableDeeplinkAction

/**
 * Открытие папки БЗ
 */
data class OpenKnowledgeFolderDeepLinkAction(
    val folderUuid: UUID
) : SerializableDeeplinkAction


/**
 * Открытие отдельной статьи БЗ
 */
data class OpenSingleArticleDeepLinkAction(
    val docUuid: UUID
) : SerializableDeeplinkAction


/**
 * SerializableDeeplinkAction для открытия переписки в службе поддержки
 */
data class OpenSupportConversationDeepLinkAction(
    val dialogUuid: UUID,
    val conversationTitle: String?
) : SerializableDeeplinkAction

/**
 * Открытие переписки в чатах CRM.
 */
data class OpenCRMConversationDeepLinkAction(
    val dialogUuid: UUID
) : SerializableDeeplinkAction

/**
 * Открытие картчоки продажи/записи в салоне
 */
data class OpenRetailOrderCardDeepLinkAction(
    val orderId: Long?
) : SerializableDeeplinkAction