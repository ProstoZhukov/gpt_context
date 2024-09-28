package ru.tensor.sbis.communication_decl.selection.recipient

import ru.tensor.sbis.communication_decl.selection.SelectionDoneButtonVisibilityMode
import ru.tensor.sbis.communication_decl.selection.SelectionHeaderMode
import ru.tensor.sbis.communication_decl.selection.SelectionHeaderMode.ADDITIONAL_SEARCH
import ru.tensor.sbis.communication_decl.selection.SelectionMode
import ru.tensor.sbis.communication_decl.selection.SelectionMode.ALWAYS_ADD
import ru.tensor.sbis.communication_decl.selection.SelectionMode.SINGLE
import ru.tensor.sbis.communication_decl.selection.SelectionUseCase
import java.util.UUID

/**
 * Use-case компонента выбора получателей.
 *
 * @author vv.chekurda
 */
sealed class RecipientSelectionUseCase(override val name: String) : SelectionUseCase {

    /**
     * Необходимость распаковывать получателей из подразделений в результате выбора.
     */
    open val unfoldDepartments: Boolean = false

    /**
     * Признак доступности выбора подразделений в качестве адресата.
     * При false в подразделение можно только провалиться.
     */
    open val isDepartmentsSelectable: Boolean = true

    /**
     * Признак необходимости импортировать результат выбора в контроллер лиц.
     */
    open val requireImportFaces: Boolean = false

    /**
     * Базовый выбор персоны среди контактов, сотрудников, поздразделений.
     */
    object Base : RecipientSelectionUseCase(BASE_USE_CASE) {
        override val unfoldDepartments: Boolean = true
    }

    /**
     * Создание нового диалога и выбор адресатов в новом диалоге.
     */
    object NewDialog : RecipientSelectionUseCase(NEW_DIALOG_USE_CASE) {
        override val unfoldDepartments: Boolean = true
    }

    /**
     * Выбор получателей в существующем диалоге.
     */
    data class Dialog(
        val dialogUuid: UUID,
        val documentUuid: UUID? = null
    ) : RecipientSelectionUseCase(DIALOG_USE_CASE) {
        override val unfoldDepartments: Boolean = true
        override val args: HashMap<String, String?>
            get() = hashMapOf(
                CONVERSATION_UUID_KEY to dialogUuid.toString(),
                DOCUMENT_UUID_KEY to documentUuid?.toString()
            )
        override val doneButtonMode = SelectionDoneButtonVisibilityMode.VISIBLE
    }

    /**
     * Создание нового приватного канала.
     */
    object NewPrivateChat : RecipientSelectionUseCase(NEW_PRIVATE_CHAT_USE_CASE) {
        override val selectionMode = SINGLE
        override val isDepartmentsSelectable: Boolean = false
    }

    /**
     * Создание нового группового канала.
     */
    object NewChat : RecipientSelectionUseCase(NEW_CHAT_USE_CASE) {
        override val unfoldDepartments: Boolean = true
    }

    /**
     * Выбор получателей в существующем канале.
     */
    data class Chat(
        val chatUuid: UUID
    ) : RecipientSelectionUseCase(CHAT_USE_CASE) {
        override val unfoldDepartments: Boolean = true
        override val args: HashMap<String, String?>
            get() = hashMapOf(
                CONVERSATION_UUID_KEY to chatUuid.toString()
            )
        override val doneButtonMode = SelectionDoneButtonVisibilityMode.VISIBLE
    }

    /**
     * Выбор получателей в чате консультации.
     */
    data class ChatConsultation(
        val chatUuid: UUID,
    ) : RecipientSelectionUseCase(CONSULTATION_CHAT_USE_CASE) {
        override val unfoldDepartments: Boolean = true
        override val args: HashMap<String, String?>
            get() = hashMapOf(
                CONVERSATION_UUID_KEY to chatUuid.toString(),
            )
        override val doneButtonMode = SelectionDoneButtonVisibilityMode.VISIBLE
        override val requireImportFaces: Boolean = true
    }

    data class ChatParticipants(
        val chatUuid: UUID
    ) : RecipientSelectionUseCase(CHAT_PARTICIPANTS_USE_CASE) {
        override val unfoldDepartments: Boolean = true
        override val args: HashMap<String, String?>
            get() = hashMapOf(CONVERSATION_UUID_KEY to chatUuid.toString())
        override val doneButtonMode = SelectionDoneButtonVisibilityMode.VISIBLE
    }

    /**
     * Добавление участников в канал.
     */
    data class AddChatParticipants(
        val chatUuid: UUID
    ) : RecipientSelectionUseCase(ADD_CHAT_PARTICIPANTS_USE_CASE) {
        override val unfoldDepartments: Boolean = true
        override val args: HashMap<String, String?>
            get() = hashMapOf(CONVERSATION_UUID_KEY to chatUuid.toString())
        override val doneButtonMode = SelectionDoneButtonVisibilityMode.VISIBLE
    }

    /**
     * Добавление администраторов в канал.
     */
    data class AddChatAdmins(
        val chatUuid: UUID
    ) : RecipientSelectionUseCase(ADD_CHAT_ADMINS_USE_CASE) {
        override val unfoldDepartments: Boolean = true
        override val args: HashMap<String, String?>
            get() = hashMapOf(CONVERSATION_UUID_KEY to chatUuid.toString())
    }

    /**
     * Создание нового диалога для шаринга.
     */
    object ShareMessages : RecipientSelectionUseCase(SHARE_MESSAGES_USE_CASE) {
        override val isFinalComplete: Boolean = false
    }

    /**
     * Пункт меню шаринга "Контакты": выбор контактов для отправки сообщения в новый диалог.
     */
    object ContactsShare : RecipientSelectionUseCase(CONTACTS_SHARE_USE_CASE) {
        override val headerMode: SelectionHeaderMode = ADDITIONAL_SEARCH
        override val isFinalComplete: Boolean = false
        override val isDepartmentsSelectable: Boolean = false
    }

    /**
     * Выбор получателей для действий над документами.
     */
    object DocumentAction : RecipientSelectionUseCase(DOCUMENT_ACTION_USE_CASE)

    /**
     * Добавление персон, которые имеют доступ к документу.
     */
    object DocumentAddAccessors : RecipientSelectionUseCase(DOCUMENT_ADD_ACCESSORS_USE_CASE)

    /**
     * Выбор персоны для фильтрации по реестру истории звонков.
     */
    object CallHistory : RecipientSelectionUseCase(CALL_HISTORY_USE_CASE) {
        override val unfoldDepartments: Boolean = true
        override val selectionMode: SelectionMode = SINGLE
    }

    /**
     * Добавление участников видеозвонка.
     */
    object VideoCallParticipants : RecipientSelectionUseCase(VIDEO_CALL_PARTICIPANTS_USE_CASE) {
        override val unfoldDepartments: Boolean = true
        override val selectionMode: SelectionMode = ALWAYS_ADD
    }

    /**
     * Выбор участников совещания.
     */
    object NewMeeting : RecipientSelectionUseCase(NEW_MEETING_USE_CASE) {
        override val unfoldDepartments: Boolean = true
        override val selectionMode: SelectionMode = ALWAYS_ADD
    }

    /**
     * Редактирование участников совещания.
     */
    object MeetingMembers : RecipientSelectionUseCase(MEETING_MEMBERS_USE_CASE) {
        override val unfoldDepartments: Boolean = true
        override val selectionMode: SelectionMode = ALWAYS_ADD
    }

    /**
     * Поделиться новостью с контактами.
     */
    data class NewsRepostContacts(val documentUuid: UUID) : RecipientSelectionUseCase(NEWS_REPOST_CONTACTS_USE_CASE) {
        override val args: HashMap<String, String?>
            get() = hashMapOf(
                DOCUMENT_UUID_KEY to documentUuid.toString()
            )
    }

    /**
     * Поделиться новостью с сотрудниками.
     */
    object NewsRepostEmployees : RecipientSelectionUseCase(NEWS_REPOST_EMPLOYEES_USE_CASE)

    /**
     * Выбор исполнителя задачи при создании.
     */
    object TaskCreate : RecipientSelectionUseCase(TASK_CREATE_USE_CASE) {
        override val requireImportFaces: Boolean = true
    }

    /**
     * Переназначение папки задач.
     */
    object TaskReassignFolder : RecipientSelectionUseCase(TASK_REASSIGN_FOLDER) {
        override val selectionMode: SelectionMode = SINGLE
        override val requireImportFaces: Boolean = true
        override val isDepartmentsSelectable = false
    }

    /**
     * Отправка задачи на согласование.
     */
    data class TaskApproval(val documentUuid: UUID) : RecipientSelectionUseCase(TASK_APPROVAL_USE_CASE) {
        override val args: HashMap<String, String?>
            get() = hashMapOf(
                DOCUMENT_UUID_KEY to documentUuid.toString()
            )

        override val selectionMode: SelectionMode = SINGLE
        override val unfoldDepartments: Boolean = true
        override val requireImportFaces: Boolean = true
    }

    /**
     * Выбор исполнителя задачи при смене фазы.
     */
    data class DocFlowPassages(val documentUuid: UUID?) : RecipientSelectionUseCase(DOC_FLOW_PASSAGES_USE_CASE) {
        override val args: HashMap<String, String?>
            get() = documentUuid?.let { hashMapOf(DOCUMENT_UUID_KEY to it.toString()) } ?: HashMap()
        override val requireImportFaces: Boolean = true
    }

    /**
     * Выбор сотрудников в доп. полях документа.
     */
    object DocSettingsExecutors : RecipientSelectionUseCase(TASK_CREATE_USE_CASE) {
        override val requireImportFaces: Boolean = true
    }

    /**
     * Выбор сотрудников в карточке командировки.
     */
    object BusinessTripEmployee : RecipientSelectionUseCase(BUSINESS_TRIP_EMPLOYEE_USE_CASE) {
        override val selectionMode: SelectionMode = SINGLE
        override val isDepartmentsSelectable: Boolean = false
    }

    /**
     * Выбор доверенного сотрудника по контрагенту.
     */
    object ContractorAuthorityEmployee : RecipientSelectionUseCase(
        CONTRACTOR_AUTHORITY_EMPLOYEE_USE_CASE
    ) {
        override val selectionMode: SelectionMode = SINGLE
        override val isDepartmentsSelectable: Boolean = false
    }

    /**
     * Выбор доверенного сотрудника по субъекту деталей доверия.
     */
    object AuthorityDetailsEmployee : RecipientSelectionUseCase(AUTHORITY_DETAILS_EMPLOYEE_USE_CASE) {
        override val selectionMode: SelectionMode = SINGLE
        override val isDepartmentsSelectable: Boolean = false
    }

    /**
     * Выбор сотрудников в карточках поощрений и взысканий.
     */
    object AchievementsRecipients : RecipientSelectionUseCase(ACHIEVEMENTS_RECIPIENTS_USE_CASE) {
        override val selectionMode: SelectionMode = ALWAYS_ADD
        override val isDepartmentsSelectable: Boolean = false
    }

    /**
     * Выбор ответственного по документу SabyDoc.
     */
    object ResponsibleDocumentEmployee : RecipientSelectionUseCase(RESPONSIBLE_DOCUMENT_EMPLOYEE_USE_CASE) {
        override val selectionMode: SelectionMode = SINGLE
        override val requireImportFaces: Boolean = true
    }

    /**
     * Выбор сотрудника(ов) на экране фильтров для входящих/исходящих документов.
     */
    data class InOutFiltersEmployee(val isSingle: Boolean) : RecipientSelectionUseCase(IN_OUT_FILTERS_EMPLOYEE_USE_CASE) {
        override val args: HashMap<String, String?>
            get() = hashMapOf(EXCLUDE_CURRENT_USER_KEY to true.toString())
        override val selectionMode: SelectionMode = if (isSingle) SINGLE else ALWAYS_ADD
        override val isDepartmentsSelectable: Boolean = false
        override val requireImportFaces: Boolean = true
    }

    /**
     * Выбор ответственного на экране сделок в фильтрах.
     */
    object ResponsibleDealsEmployee : RecipientSelectionUseCase(RESPONSIBLE_DEALS_EMPLOYEE) {
        override val args: HashMap<String, String?>
            get() = hashMapOf(EXCLUDE_CURRENT_USER_KEY to false.toString())
        override val selectionMode: SelectionMode = SINGLE
        override val isDepartmentsSelectable: Boolean = true
    }

    /**
     * Выбор ответственного на экране списка машин.
     */
    object CarDriver : RecipientSelectionUseCase(RESPONSIBLE_EMPLOYEES) {
        override val selectionMode: SelectionMode = SINGLE
        override val requireImportFaces: Boolean = true
        override val isDepartmentsSelectable = false
    }

    /**
     * Выбор ответственного оператора на экране фильтров чатов crm.
     */
    object CRMEmployees : RecipientSelectionUseCase(RESPONSIBLE_EMPLOYEES) {
        override val headerMode: SelectionHeaderMode = ADDITIONAL_SEARCH
    }

    /**
     * Выбор ответственного на экране фильтра реестра клиентов и в карточке клиента.
     */
    object ClientResponsibleEmployee : RecipientSelectionUseCase(CLIENT_RESPONSIBLE_EMPLOYEE) {
        override val selectionMode: SelectionMode = SINGLE
        override val isDepartmentsSelectable: Boolean = false
    }

    /**
     * Выбор персоны только текущего аккаунта для фильтрации по реестру.
     */
    object InternalEmployees : RecipientSelectionUseCase(ONLY_INTERNAL_EMPLOYEES) {
        override val selectionMode: SelectionMode = ALWAYS_ADD
        override val isDepartmentsSelectable: Boolean = false
    }

    /**
     * Выбор электронных очередей.
     */
    data class ElectronicQueues(
        val clientId: Int, val salePointId: Int
    ) : RecipientSelectionUseCase(ELECTRONIC_QUEUES) {
        override val args: HashMap<String, String?>
            get() = hashMapOf(CLIENT_ID_KEY to clientId.toString(), SALE_POINT_ID_KEY to salePointId.toString())
        override val selectionMode: SelectionMode = ALWAYS_ADD
        override val isDepartmentsSelectable: Boolean = false
        override val itemsLimit: Int? = null
    }
}

// Константы значений use_case выбора получателей.
private const val BASE_USE_CASE = "contacts_employees_departments_workgroups"
private const val NEW_DIALOG_USE_CASE = "new_dialog"
private const val DIALOG_USE_CASE = "dialog"
private const val NEW_PRIVATE_CHAT_USE_CASE = "new_private_chat"
private const val NEW_CHAT_USE_CASE = "new_chat"
private const val CHAT_USE_CASE = "chat"
private const val CHAT_PARTICIPANTS_USE_CASE = "chat_participants"
private const val ADD_CHAT_PARTICIPANTS_USE_CASE = "add_chat_participants"
private const val ADD_CHAT_ADMINS_USE_CASE = "add_chat_admins"
private const val CONSULTATION_CHAT_USE_CASE = "consultation_chat"
private const val DOCUMENT_ACTION_USE_CASE = "document_action"
private const val DOCUMENT_ADD_ACCESSORS_USE_CASE = "document_add_accessors"
private const val CALL_HISTORY_USE_CASE = "call_history_employee_filter"
private const val VIDEO_CALL_PARTICIPANTS_USE_CASE = "video_call_participants"
private const val NEW_MEETING_USE_CASE = "new_meeting"
private const val MEETING_MEMBERS_USE_CASE = "meeting_members"
private const val NEWS_REPOST_CONTACTS_USE_CASE = "news_share_contacts"
private const val NEWS_REPOST_EMPLOYEES_USE_CASE = "news_share_departments"
private const val TASK_CREATE_USE_CASE = "task_create"
private const val TASK_REASSIGN_FOLDER = "task_reassign_folder"
private const val TASK_APPROVAL_USE_CASE = "task_approval"
private const val DOC_FLOW_PASSAGES_USE_CASE = "docflow_passages"
private const val BUSINESS_TRIP_EMPLOYEE_USE_CASE = "business_trip_employee"
private const val CONTRACTOR_AUTHORITY_EMPLOYEE_USE_CASE = "contractor_authority_employee"
private const val AUTHORITY_DETAILS_EMPLOYEE_USE_CASE = "authority_details_employee"
private const val ACHIEVEMENTS_RECIPIENTS_USE_CASE = "achievement_recipients"
private const val RESPONSIBLE_DOCUMENT_EMPLOYEE_USE_CASE = "responsible_document_employee"
private const val IN_OUT_FILTERS_EMPLOYEE_USE_CASE = "in_out_filters_employee"
private const val SHARE_MESSAGES_USE_CASE = "share_new_dialog"
private const val CONTACTS_SHARE_USE_CASE = "share_new_dialog"
private const val RESPONSIBLE_DEALS_EMPLOYEE = IN_OUT_FILTERS_EMPLOYEE_USE_CASE
private const val RESPONSIBLE_EMPLOYEES = "responsible_employees"
private const val ONLY_INTERNAL_EMPLOYEES = "only_internal_employees"
private const val ELECTRONIC_QUEUES = "electronic_queues"
private const val CLIENT_RESPONSIBLE_EMPLOYEE = RESPONSIBLE_EMPLOYEES

// Константы аргументов use_case выбора получателей.
private const val CONVERSATION_UUID_KEY = "theme_uuid"
private const val DOCUMENT_UUID_KEY = "document_uuid"
private const val CLIENT_ID_KEY = "client_id"
private const val SALE_POINT_ID_KEY = "sale_point_id"
private const val EXCLUDE_CURRENT_USER_KEY = "exclude_current_user"
