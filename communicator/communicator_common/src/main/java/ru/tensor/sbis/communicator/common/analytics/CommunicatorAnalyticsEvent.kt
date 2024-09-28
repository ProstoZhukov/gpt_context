package ru.tensor.sbis.communicator.common.analytics

import android.os.Bundle
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communication_decl.analytics.AnalyticsEvent
import ru.tensor.sbis.communicator.declaration.model.ChatType
import ru.tensor.sbis.communicator.declaration.model.DialogType
import ru.tensor.sbis.communicator.generated.ConversationType
import java.util.*

/**
 * События аналитики в реестрах диалогов и каналов.
 */
open class ThemeAnalyticsEvent(
    override val functional: String,
    override val event: String,
) : AnalyticsEvent {

    override val analyticContext: String = COMMUNICATOR_ANALYTICS_THEME_SCREEN

    /**
     * Переключение реестра диалоги/каналы.
     */
    class SwitchMessageRee(
        functional: String,
        private val typeRee: ConversationType,
    ) : ThemeAnalyticsEvent(
        functional,
        COMMUNICATOR_ANALYTICS_SWITCH_MESSAGE_REE,
    ) {
        override val bundle: Bundle
            get() {
                val bundle = Bundle()
                val reeName = if (typeRee == ConversationType.CHAT) {
                    ReeAnalytic.CHATS.reeName
                } else {
                    ReeAnalytic.DIALOGS.reeName
                }
                bundle.putString(COMMUNICATOR_ANALYTICS_REE_KEY, reeName)
                return bundle
            }
    }

    /**
     * Смена фильтра в реестре диалогов.
     */
    class ChangeDialogsFilter(
        functional: String,
        private val filter: DialogType,
    ) : ThemeAnalyticsEvent(
        functional,
        COMMUNICATOR_ANALYTICS_CHANGE_DIALOGS_FILTER,
    ) {
        override val bundle: Bundle
            get() {
                val bundle = Bundle()
                bundle.putString(
                    COMMUNICATOR_ANALYTICS_FILTER_KEY,
                    when (filter) {
                        DialogType.ALL -> DialogsAnalyticFilter.ALL.filterName
                        DialogType.INCOMING -> DialogsAnalyticFilter.INCOMING.filterName
                        DialogType.UNREAD -> DialogsAnalyticFilter.UNREAD.filterName
                        DialogType.UNANSWERED -> DialogsAnalyticFilter.UNANSWERED.filterName
                        DialogType.DELETED -> DialogsAnalyticFilter.DELETED.filterName
                    },
                )
                return bundle
            }
    }

    /**
     * Смена фильтра в реестре каналов.
     */
    class ChangeChatsFilter(
        functional: String,
        private val filter: ChatType,
    ) : ThemeAnalyticsEvent(
        functional,
        COMMUNICATOR_ANALYTICS_CHANGE_CHANNELS_FILTER,
    ) {
        override val bundle: Bundle
            get() {
                val bundle = Bundle()
                bundle.putString(
                    COMMUNICATOR_ANALYTICS_FILTER_KEY,
                    when (filter) {
                        ChatType.ALL -> ChatsAnalyticFilter.ALL.filterName
                        ChatType.UNREAD -> ChatsAnalyticFilter.UNREAD.filterName
                        ChatType.HIDDEN -> ChatsAnalyticFilter.HIDDEN.filterName
                    },
                )
                return bundle
            }
    }

    /**
     * Открытие окна папок в диалогах.
     */
    class OpenedDialogsFolders(functional: String) : ThemeAnalyticsEvent(
        functional,
        COMMUNICATOR_ANALYTICS_OPENED_DIALOGS_FOLDERS,
    )

    /**
     * Переход в папку в диалогах.
     */
    class GoToFoldersDialogs(functional: String) : ThemeAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_DIALOGS_GO_TO_FOLDERS)

    /**
     * Перемещение контакта в папку в диалогах свайпом.
     */
    class MoveDialogsToFoldersBySwipe(functional: String) : ThemeAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_SWIPE_MOVE_THEME_TO_FOLDER)

    /**
     * Перемещение контакта в папку в диалогах массовыми операциями.
     */
    class MoveDialogsToFoldersByMassOperation(functional: String) : ThemeAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_MASS_OPERATION_MOVE_THEME_TO_FOLDER)

    /**
     * Открытие массовых операций в диалогах.
     */
    class DialogsChooseMassOperation(functional: String) : ThemeAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_DIALOGS_CHOOSE_MASS_OPERATION)

    /**
     * Удаление темы через свайп.
     */
    class SwipeRemoveTheme(functional: String) : ThemeAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_SWIPE_REMOVE_THEME)

    /**
     * Удаление темы через массовые операции.
     */
    class MassOperationRemoveTheme(functional: String) : ThemeAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_MASS_OPERATION_REMOVE_THEME)

    /**
     * Восстановление темы из удаленных.
     */
    class RecoverTheme(functional: String) : ThemeAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_RECOVER_THEME)

    /**
     * Пометка непрочитанным через массовые операции.
     */
    class MassOperationUnreadTheme(functional: String) : ThemeAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_MASS_OPERATION_UNREAD_THEME)

    /**
     * Пометка прочитанным через массовые операции.
     */
    class MassOperationReadTheme(functional: String) : ThemeAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_MASS_OPERATION_READ_THEME)

    /**
     * Пометка непрочитанным через свайп.
     */
    class SwipeUnreadTheme(functional: String) : ThemeAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_SWIPE_UNREAD_THEME)

    /**
     * Пометка прочитанным через массовые операции свайп.
     */
    class SwipeReadTheme(functional: String) : ThemeAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_SWIPE_READ_THEME)

    /**
     * Выбор из горизонтального саггеста.
     */
    class ChooseFromPersonSuggestView(functional: String) : ThemeAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_CHOOSE_PERSON_FROM_HORIZONTAL_SUGGEST)

    /**
     * Начало поиска в реестре диалогов.
     */
    class SearchDialogs(functional: String) : ThemeAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_SEARCH_IN_DIALOGS)

    /**
     * Начало поиска в реестре каналов.
     */
    class SearchChats(functional: String) : ThemeAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_SEARCH_IN_CHANNELS)

    /**
     * Просмотр вложений в карусели диалога.
     */
    class ViewingAttachmentsInDialogCarousel(functional: String) : ThemeAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_VIEWING_ATTACHMENTS_IN_DIALOG_CAROUSEL)
}

/**
 * События аналитики в реестре контактов.
 */
open class ContactsAnalyticsEvent(
    override val functional: String,
    override val event: String,
) : AnalyticsEvent {

    override val analyticContext: String = COMMUNICATOR_ANALYTICS_CONTACTS_SCREEN

    /**
     * Переход в реестр контактов.
     */
    class OpenContactsRee(functional: String) : ContactsAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_OPEN_CONTACTS_REE)

    /**
     * Смена фильтра в реестре контактов.
     */
    class ChangeContactsFilter(
        functional: String,
        private val filterStringId: Int,
    ) : ContactsAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_CHANGE_CONTACTS_FILTER) {
        override val bundle: Bundle
            get() {
                val bundle = Bundle()
                val filterName = if (filterStringId == ru.tensor.sbis.communicator.design.R.string.communicator_spinner_item_sort_by_date) {
                    ContactsAnalyticFilter.BY_LAST_MESSAGE_DATE.filterName
                } else {
                    ContactsAnalyticFilter.BY_NAME.filterName
                }
                bundle.putString(COMMUNICATOR_ANALYTICS_FILTER_KEY, filterName)
                return bundle
            }
    }

    /**
     * Начало поиска в реестре контактов.
     */
    class SearchContacts(functional: String) : ContactsAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_SEARCH_CONTACTS)

    /**
     * Открытие окна папок в реестре контактов. (Нажатие на иконку папок слева, а не проваливание)
     */
    class OpenedFoldersContacts(functional: String) : ContactsAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_OPENED_CONTACTS_FOLDERS)

    /**
     * Переход в папку в реестре контактов.
     */
    class GoToFoldersContacts(functional: String) : ContactsAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_CONTACTS_GO_TO_FOLDERS)

    /**
     * Перемещение контакта в папку в реестре контактов.
     */
    class MoveContactsToFolders(functional: String) : ContactsAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_MOVE_CONTACTS_TO_FOLDERS)

    /**
     * Импорт контактов.
     */
    class ImportContacts(functional: String) : ContactsAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_IMPORT_CONTACTS)

    /**
     * Открытие "Контакты внутри компании".
     */
    class OpenContactsInCompany(functional: String) : ContactsAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_OPEN_CONTACTS_IN_COMPANY)

    /**
     * Открытие "Найти новый контакт".
     */
    class OpenFindNewContacts(functional: String) : ContactsAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_OPEN_FIND_NEW_CONTACTS)
}

/**
 * События аналитики при шаринге.
 */
open class ShareAnalyticsEvent(
    override val functional: String,
    override val event: String,
) : AnalyticsEvent {

    override val analyticContext: String = COMMUNICATOR_ANALYTICS_SHARE_SCREEN

    /**
     * Завершение шаринга.
     */
    class SentMessageFromSharedExtension(
        functional: String,
        private val conversationId: UUID,
    ) : ShareAnalyticsEvent(functional, COMMUNICATOR_ANALYTICS_SENT_MESSAGE_FROM_SHARED_EXTENSION) {
        override val bundle: Bundle
            get() {
                val bundle = Bundle()
                bundle.putSerializable(COMMUNICATOR_ANALYTICS_SHARE_CONVERSATION_ID_KEY, conversationId)
                return bundle
            }
    }
}

/**
 * События аналитики для работы операторов с чатами CRM.
 */
open class CRMChatWorkEvent(
    override val analyticContext: String,
) : AnalyticsEvent {
    override val functional: String = COMMUNICATOR_ANALYTICS_CRM_CHAT_WORK
    override val event: String = StringUtils.EMPTY

    /**
     * Взятие консультации(чата) в работу.
     */
    object TakeConsultation : CRMChatWorkEvent(COMMUNICATOR_ANALYTICS_CRM_TAKE_CONSULTATION)

    /**
     * Завершение консультации(чата).
     */
    object CompleteConsultation : CRMChatWorkEvent(COMMUNICATOR_ANALYTICS_CRM_COMPLETE_CONSULTATION)

    /**
     * Открытие шторки с историей общения по клиенту.
     */
    object OpenHistoryView : CRMChatWorkEvent(COMMUNICATOR_ANALYTICS_CRM_OPEN_HISTORY_VIEW)

    /**
     * Переназначение консультации каналу.
     *
     * @param target куда производим переназначение.
     */
    class ReassignConsultation(val target: ReassignConsultationTarget) :
        CRMChatWorkEvent(COMMUNICATOR_ANALYTICS_CRM_REASSIGN_CONSULTATION) {
        override val event = target.value
    }
}

/**
 * Выбор быстрого ответа.
 *
 * @param quickReplyListCalledByButton был ли список быстрых ответов открыт кнопкой.
 * @param isPinned являлся ли быстрый ответ закрепленным (используется если ответ из списка вызванного кнопкой).
 */
class QuickReply(
    private val quickReplyListCalledByButton: Boolean,
    val isPinned: Boolean
) : AnalyticsEvent {
    override val functional: String = COMMUNICATOR_ANALYTICS_CRM_QUICK_REPLY
    override val analyticContext: String = StringUtils.EMPTY
    override val event: String = "${getCallSource()};${getPinned()}"

    private fun getCallSource(): String =
        if (quickReplyListCalledByButton) QUICK_REPLY_CALL_SOURCE_BUTTON else QUICK_REPLY_CALL_SOURCE_INPUT
    private fun getPinned(): String = when {
        !quickReplyListCalledByButton -> StringUtils.EMPTY
        isPinned -> QUICK_REPLY_PINNED
        else -> QUICK_REPLY_NOT_PINNED
    }
}

/**
 * Выбор быстрого приветствия.
 *
 * @param selectedWithTakeConsultation при выборе приветствия взялась ли автоматически консультация в работу.
 */
class QuickGreeting(
    private val selectedWithTakeConsultation: Boolean,
) : AnalyticsEvent {
    override val functional: String = COMMUNICATOR_ANALYTICS_CRM_QUICK_GREETING
    override val analyticContext: String = StringUtils.EMPTY
    override val event: String = getWhenSelected()

    private fun getWhenSelected() =
        if (selectedWithTakeConsultation) QUICK_GREETING_BEFORE_SELECTED else QUICK_GREETING_AFTER_SELECTED
}

//region DialogsAndChatsConst
private const val COMMUNICATOR_ANALYTICS_SWITCH_MESSAGE_REE = "switch_messages_ree"
private const val COMMUNICATOR_ANALYTICS_CHANGE_DIALOGS_FILTER = "change_dialogs_filter"
private const val COMMUNICATOR_ANALYTICS_CHANGE_CHANNELS_FILTER = "change_channels_filter"
private const val COMMUNICATOR_ANALYTICS_OPENED_DIALOGS_FOLDERS = "opened_dialogs_folders"
private const val COMMUNICATOR_ANALYTICS_DIALOGS_GO_TO_FOLDERS = "dialogs_go_to_folders"
private const val COMMUNICATOR_ANALYTICS_SWIPE_MOVE_THEME_TO_FOLDER = "swipe_move_theme_to_folder"
private const val COMMUNICATOR_ANALYTICS_MASS_OPERATION_MOVE_THEME_TO_FOLDER = "mass_operation_move_theme_to_folder"
private const val COMMUNICATOR_ANALYTICS_DIALOGS_CHOOSE_MASS_OPERATION = "dialogs_choose_mass_operation"
private const val COMMUNICATOR_ANALYTICS_SWIPE_REMOVE_THEME = "swipe_remove_theme"
private const val COMMUNICATOR_ANALYTICS_MASS_OPERATION_REMOVE_THEME = "mass_operation_remove_theme"
private const val COMMUNICATOR_ANALYTICS_RECOVER_THEME = "recover_theme"
private const val COMMUNICATOR_ANALYTICS_MASS_OPERATION_UNREAD_THEME = "mass_operation_unread_theme"
private const val COMMUNICATOR_ANALYTICS_MASS_OPERATION_READ_THEME = "mass_operation_read_theme"
private const val COMMUNICATOR_ANALYTICS_SWIPE_UNREAD_THEME = "swipe_unread_theme"
private const val COMMUNICATOR_ANALYTICS_SWIPE_READ_THEME = "swipe_read_theme"
private const val COMMUNICATOR_ANALYTICS_CHOOSE_PERSON_FROM_HORIZONTAL_SUGGEST = "choose_person_from_horizontal_suggest"
private const val COMMUNICATOR_ANALYTICS_SEARCH_IN_DIALOGS = "search_in_dialogs"
private const val COMMUNICATOR_ANALYTICS_SEARCH_IN_CHANNELS = "search_in_channels"
private const val COMMUNICATOR_ANALYTICS_VIEWING_ATTACHMENTS_IN_DIALOG_CAROUSEL = "viewing_attachments_in_dialog_carousel"
//endregion

//region ContactsConst
private const val COMMUNICATOR_ANALYTICS_OPEN_CONTACTS_REE = "open_contacts_ree"
private const val COMMUNICATOR_ANALYTICS_CHANGE_CONTACTS_FILTER = "change_contacts_filter"
private const val COMMUNICATOR_ANALYTICS_SEARCH_CONTACTS = "search_contacts"
private const val COMMUNICATOR_ANALYTICS_OPENED_CONTACTS_FOLDERS = "opened_contacts_folders"
private const val COMMUNICATOR_ANALYTICS_CONTACTS_GO_TO_FOLDERS = "contacts_go_to_folders"
private const val COMMUNICATOR_ANALYTICS_MOVE_CONTACTS_TO_FOLDERS = "move_contacts_to_folders"
private const val COMMUNICATOR_ANALYTICS_IMPORT_CONTACTS = "import_contacts"
private const val COMMUNICATOR_ANALYTICS_OPEN_CONTACTS_IN_COMPANY = "open_contacts_in_company"
private const val COMMUNICATOR_ANALYTICS_OPEN_FIND_NEW_CONTACTS = "open_find_new_contacts"
//endregion

//region ShareConst
private const val COMMUNICATOR_ANALYTICS_SENT_MESSAGE_FROM_SHARED_EXTENSION = "sent_message_from_shared_extension"
//endregion

//region CRMConst
private const val COMMUNICATOR_ANALYTICS_CRM_CHAT_WORK = "Работа операторов с чатами"
private const val COMMUNICATOR_ANALYTICS_CRM_QUICK_REPLY = "Быстрые ответы"
private const val COMMUNICATOR_ANALYTICS_CRM_QUICK_GREETING = "Быстрые приветствия"
private const val COMMUNICATOR_ANALYTICS_CRM_TAKE_CONSULTATION = "Взятие чата в работу"
private const val COMMUNICATOR_ANALYTICS_CRM_COMPLETE_CONSULTATION = "Завершение чата"
private const val COMMUNICATOR_ANALYTICS_CRM_REASSIGN_CONSULTATION = "Переназначение чата"
private const val COMMUNICATOR_ANALYTICS_CRM_OPEN_HISTORY_VIEW = "Открытие шторки с историей общения по клиенту"
private const val QUICK_REPLY_CALL_SOURCE_BUTTON = "По кнопке"
private const val QUICK_REPLY_CALL_SOURCE_INPUT = "По вводу"
private const val QUICK_REPLY_PINNED = "Закрепленный"
private const val QUICK_REPLY_NOT_PINNED = "Не закрепленный"
private const val QUICK_GREETING_BEFORE_SELECTED = "До взятия чата"
private const val QUICK_GREETING_AFTER_SELECTED = "После взятия чата"
//endregion

//region Screens
private const val COMMUNICATOR_ANALYTICS_THEME_SCREEN = "Dialog_chat_screen"
private const val COMMUNICATOR_ANALYTICS_CONTACTS_SCREEN = "Contacts_screen"
private const val COMMUNICATOR_ANALYTICS_SHARE_SCREEN = "Share_screen"
//endregion

//region Bundle keys
private const val COMMUNICATOR_ANALYTICS_FILTER_KEY = "filter"
private const val COMMUNICATOR_ANALYTICS_REE_KEY = "reeName"
private const val COMMUNICATOR_ANALYTICS_SHARE_CONVERSATION_ID_KEY = "Conversation_id"
//endregion
