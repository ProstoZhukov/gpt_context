package ru.tensor.sbis.communication_decl.crm

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.util.UUID

/**
 * Модель параметров для открытия/создания чата CRM.
 *
 * @property crmConsultationCase сценарий чата-консультации.
 * @property needBackButton нужно ли отобразить стрелку "назад" в тулбаре в мобильной версии,
 * в планшетной всегда скрываем.
 * @property hasAccordion отображается ли аккардеон. Актуально только для брендов.
 * Когда открываем конкретный чат всегда false, иначе по наличию.
 * @property isSwipeBackEnabled доступен ли свайпбек для закрытия экрана.
 * @property needOpenKeyboard необходимо ли сразу поднять клавиатуру.
 *
 * @author da.zhukov
 */
sealed interface CRMConsultationParams : Serializable, Parcelable {
    val crmConsultationCase: CRMConsultationCase
    val needBackButton: Boolean
    val hasAccordion: Boolean
    val isSwipeBackEnabled: Boolean
    val needOpenKeyboard: Boolean
}

/**
 * Параметры для открытия существующего чата CRM.
 * @property relevantMessageUuid идентификатор релевантного сообщения.
 * @property isCompleted true, если завершенная консультация.
 * @property isHistoryMode true если переписка открылась из шторки истории консультаций.
 * @property isMessagePanelVisible false, если необходимо скрыть панель ввода.
 */
@Parcelize
class CRMConsultationOpenParams(
    val relevantMessageUuid: UUID? = null,
    val isCompleted: Boolean = false,
    val isHistoryMode: Boolean = false,
    override val needBackButton: Boolean = true,
    override val crmConsultationCase: CRMConsultationCase,
    override val hasAccordion: Boolean = false,
    override val isSwipeBackEnabled: Boolean = true,
    override val needOpenKeyboard: Boolean = false,
    val isMessagePanelVisible: Boolean = true
) : CRMConsultationParams

/**
 * Параметры для создания чата CRM.
 * @property consultationName название консультации.
 * @property photoUrl url для отображения иконки в шапке.
 */
@Parcelize
class CRMConsultationCreationParams(
    override val needBackButton: Boolean = true,
    val consultationName: String? = null,
    val photoUrl: String? = null,
    override val crmConsultationCase: CRMConsultationCase,
    override val hasAccordion: Boolean = false,
    override val isSwipeBackEnabled: Boolean = true,
    override val needOpenKeyboard: Boolean = true,
) : CRMConsultationParams

/**
 * Сценарий открытия или создания переписки по консультациям.
 */
sealed interface CRMConsultationCase : Parcelable {

    /**
     * Идентификатор для создания или открытия переписки.
     * Может быть идентификатором следующих сущностей: консультации, источника, точки продаж, обращения.
     */
    val originUuid: UUID

    /**
     * Не задано.
     */
    @Parcelize
    data class Unknown(
        override val originUuid: UUID = UUID(0, 0)
    ) : CRMConsultationCase
    /**
     * Консультация со стороны оператора.
     * @property viewId идентификатор отображаемого списка консультаций(необходим для правильной фильтрации консультаций по кнопке следующая).
     * @property isForReclamation true если создается консультация для обращения.
     * @property contactId идентификатор контакта консультации, для которого создается новый чат.
     * @property channelType тип контакта консультации, возможные значения channel или contact.
     */
    @Parcelize
    data class Operator(
        override val originUuid: UUID,
        val viewId: UUID,
        val isForReclamation: Boolean = false,
        val contactId: UUID? = null,
        val channelType: CrmChannelType? = null
    ) : CRMConsultationCase

    /**
     * Консультация со стороны клиента, техподдержка.
     */
    @Parcelize
    data class Client(override val originUuid: UUID) : CRMConsultationCase

    /**
     * Консультация в разрезе точки продаж.
     * @property isSabyget true если используется для сабигета.
     * @property isBrand true если используется для брендов.
     */
    @Parcelize
    data class SalePoint(
        override val originUuid: UUID,
        val isSabyget: Boolean = true,
        val isBrand: Boolean = false
    ) : CRMConsultationCase
}

/**
 * Тип канала CRM.
 */
enum class CrmChannelType {
    /**
     * Папки в нашей терминологии (во внешней терминологии- это "каналы").
     */
    CHANNEL_FOLDER,
    /**
     * Канал в нашей терминологии (во внешней терминологии- это "источники" или "подключение").
     */
    CHANNEL,
    /**
     * Линия службы поддержки (во внешней терминологии - тоже "линия службы").
     */
    OPEN_LINE,
    /**
     * Контакт истоника подключения (например email адрес для email подключения).
     */
    CONTACT,
    /**
     * Группа папок в нашей терминологии (во внешней терминологии- это "группа каналов").
     */
    CHANNEL_FOLDER_GROUP,
    /**
     * Тип подключений (Телеграм, email, VKонтакте и тп).
     */
    CHANNEL_GROUP_TYPE
}
