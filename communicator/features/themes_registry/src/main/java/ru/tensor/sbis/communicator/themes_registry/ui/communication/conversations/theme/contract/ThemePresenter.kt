package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.contract

import android.os.Bundle
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.base_components.adapter.OnItemClickListener
import ru.tensor.sbis.base_components.adapter.contacts.holder.OnContactPhotoClickListener
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.common.themes_registry.DialogListActionsListener
import ru.tensor.sbis.communicator.core.contract.AttachmentClickListener
import ru.tensor.sbis.communicator.declaration.model.EntitledItem
import ru.tensor.sbis.communicator.themes_registry.router.theme.ThemeRouterInitializer
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationItemClickHandler
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.holders.ChatListActionsListener
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.ThemeBottomCheckAction
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.ThemeListFilter
import ru.tensor.sbis.deeplink.DeeplinkActionNode
import ru.tensor.sbis.design.folders.support.listeners.FolderActionListener
import ru.tensor.sbis.design.person_suggest.service.PersonSuggestData
import ru.tensor.sbis.mvp.search.SearchablePresenter
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import java.util.UUID

/**
 * Презентер реестра диалогов/чатов.
 *
 * @author rv.krohalev
 */
internal interface ThemePresenter :
    SearchablePresenter<ThemeView>,
    ConversationItemClickHandler,
    ChatListActionsListener,
    OnItemClickListener<ContactVM>,
    OnContactPhotoClickListener<ContactVM>,
    DialogListActionsListener,
    AttachmentClickListener,
    ThemeRouterInitializer,
    DeeplinkActionNode,
    FolderActionListener {

    /**
     * Установить выбранную вкладку диалогов или чатов.
     * @param navxId Id выбранной вкладки.
     */
    fun onBranchTypeTabClick(navxId: NavxIdDecl)

    /**
     * Клик по выбору фильтра.
     */
    fun onFilterClick()

    /**
     * Клик на фаб для создания нового диалога/чата.
     */
    fun onNewDialogClick()

    /**
     * Клик на сброс строки поиска или контакта.
     * @param needSearchRequest true, если необходимо выполнить поисковый запрос при сбросе.
     */
    fun onCancelSearchClick(needSearchRequest: Boolean = true)

    /**
     * Клик по персоне из панели выбора персоны.
     * @param person выбранная персона.
     */
    fun onPersonSuggestClick(person: PersonSuggestData)

    /**
     * Клик по view персоны в фильтре.
     * @param personUuid идентификатор персоны.
     */
    fun onPersonFilterViewClicked(personUuid: UUID)

    /**
     * Обработка подтверждения скрытия чата из списка.
     * @param isConfirmed если подтверждено, то скрываем, иначе обновляем элемент на случай свайпа.
     */
    fun onHideChatConfirmationAlertClicked(isConfirmed: Boolean)

    /**
     * Смена фильтра на диалоги/чаты.
     * @param type ChatType или DialogType.
     */
    fun onThemeTypeSelected(type: EntitledItem)

    /**
     * Клик по кнопке возвращения в начало реестра.
     */
    fun onScrollToTopPressed()

    /**
     * Отметить выбранные диалоги как прочитанные/непрочитанные.
     * @param read отметить прочитанными/непрочитанными.
     */
    fun markDialogsAsRead(read: Boolean)

    /**
     * Выбрана операция перенести выбранные диалоги/чаты.
     */
    fun onMoveGroupOperationClicked()

    /**
     * Удалить диалог при тапе на айтем из свайп панели.
     */
    fun deleteDialogsByPanel()

    /**
     * Выключить режим выбора.
     */
    fun onCheckModeCancelClicked()

    /**
     * Удалить выбранные диалоги.
     */
    fun deleteDialogs(forAll: Boolean = false)

    /**
     * Выбрать корневую папку реестра.
     */
    fun onRootFolderSelected()

    /**
     * Сбросить фильтр непрочитанных.
     */
    fun resetTypeIfUnanswered()

    /**
     * Перенести выбранные диалоги в папку.
     * @param folderUuid UUID папки.
     * @param folderTitle заголовок папки.
     */
    fun moveDialogsToFolder(folderUuid: UUID, folderTitle: String = StringUtils.EMPTY)

    /**
     * Выделить в списке и открыть диалог.
     * @param conversationModel выбранная переписка.
     */
    fun selectItem(conversationModel: ConversationModel)

    /**
     * Сменить выбранный диалог в списке.
     * @param conversationModel выбранная переписка.
     */
    fun changeSelection(conversationModel: ConversationModel)

    /**
     * Удалить или скрыть переписку, если это чат.
     * @param uuid идентификатор удаляемого диалога/чата.
     * @param isChat true, если удаляем чат.
     */
    fun onDialogDismissed(uuid: UUID, isChat: Boolean)

    /**
     * @return true, если открыта вкладка чатов.
     */
    fun isChannelTab(): Boolean

    /**
     * @return размер списка найденных контактов в реестре диалогов.
     */
    fun getContactListSize(): Int

    /**
     * Устанавливает в [ThemeListFilter] минимально необходимое число диалогов для первой порции результатов поискового запроса.
     */
    fun setDialogItemsMinCount(count: Int)

    /**
     * Переход назад.
     */
    fun onBackPressed(): Boolean

    /**
     * Подтверждения номера телефона.
     */
    fun onPhoneVerificationRequired()

    /**
     * Происходит переключение вкладки диалоги / чаты.
     */
    fun isBranchTypeChanging(): Boolean = false

    /**
     * Принудительное включение или выключение отображения панели папок в реестре диалогов.
     */
    fun setFoldersEnabled(enabled: Boolean)

    /**
     * Попытаться установить папки из кэша, если они уже загружены.
     */
    fun trySetFoldersSync()

    /**
     * Перемещение диалога в новую созданную папку из корневой.
     */
    fun moveDialogToNewFolder()

    /**
     * Проверка на реестр удаленных, чтобы урезать функционал массовых операций.
     */
    fun isDeleted(): Boolean

    /**
     * Подготовка действий для нижней панели массовых операций.
     */
    fun prepareThemeBottomCheckAction(): List<ThemeBottomCheckAction>

    /**
     * Видимость вью для пользователя изменилась.
     */
    fun onViewVisibilityChanged(isInvisible: Boolean)

    /**
     * Отправить аналитику при смене вкладки с её конкретным названием.
     */
    fun sendAnalyticSwitchRee()

    /**
     * Отправить аналитику при вызове окна папок.
     */
    fun sendAnalyticOpenedDialogsFolders()

    /**
     * Подписаться на изменения доступных табов.
     * Если диалоги - единственный доступный таб, то меняем текст *диалоги* на *сообщения*.
     */
    fun collectAvailableTabs()

    /**
     * Проверить доступен ли импорт контактов. (Подключен ли модуль импорта).
     */
    fun isImportContactsAllowed(): Boolean

    /**
     * Автоимпортировать контакты, если это возможно и разрешено.
     */
    fun tryAutoImportContacts(savedInstanceState: Bundle? = null)

    /**
     * Подписаться на обновления, приходящие с ProfileSettingsController.
     */
    fun subscribeOnProfileSettingsDataRefreshed()

    /**
     * Обработать действие из меню предпросмотра.
     */
    fun handleConversationPreviewAction(conversationPreviewMenuAction: ConversationPreviewMenuAction.ThemeConversationPreviewMenuAction)
}
