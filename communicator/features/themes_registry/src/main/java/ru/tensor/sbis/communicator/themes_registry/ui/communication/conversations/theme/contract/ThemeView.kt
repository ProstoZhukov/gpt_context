package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.contract

import androidx.annotation.IntRange
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.communication_decl.complain.data.ComplainUseCase
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.common.themes_registry.ConversationOpener
import ru.tensor.sbis.communicator.common.themes_registry.ThemesRegistry
import ru.tensor.sbis.communicator.declaration.model.EntitledItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationListAdapter
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.delegates.stubs.Stubs
import ru.tensor.sbis.communicator.themes_registry.ui.communication.filters.ConversationFilterConfiguration
import ru.tensor.sbis.design.person_suggest.service.PersonSuggestData
import ru.tensor.sbis.mvp.search.SearchableView
import ru.tensor.sbis.persons.ConversationRegistryItem
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import java.util.UUID

/**
 * Контракт, определяющий поведение View реестра диалогов/чатов.
 *
 * @author rv.krohalev
 */
internal interface ThemeView : SearchableView<ConversationRegistryItem>,
    ConversationOpener,
    ThemesRegistry {

    /**
     * Адаптер реестра.
     */
    val adapter: ConversationListAdapter

    /**
     * Закрытие свайп панели холдера.
     * @param uuid UUID диалога/чата.
     */
    fun hideSwipePanel(uuid: UUID?)

    /**
     * Установка заголовка папки.
     * @param title название папки.
     */
    fun setFolderTitle(title: String?)

    /**
     * Свернуть список папок.
     */
    fun setFoldersCompact()

    /**
     * Показать выбор папки для переноса диалога.
     */
    fun showFolderSelection(currentFolder: UUID?)

    /**
     * Закрытие свайп панели при скролле.
     */
    fun hideSwipePanelOnScroll()

    /**
     * Закрытие всех свайп панелей.
     */
    fun hideAllSwipedPanels()

    /**
     * Очистить состояние свайп-меню.
     */
    fun clearSwipeMenuState()

    /**
     * Изменение состояния выбора диалогов/чатов.
     * @param hasCheckedDialogs выделены ли диалоги.
     * @param canReadDialogs    выделены диалоги, которые можно прочитать.
     * @param canUnreadDialogs  выделены диалоги, которые можно сделать не прочитанными.
     */
    fun onCheckStateChanged(hasCheckedDialogs: Boolean, canReadDialogs: Boolean, canUnreadDialogs: Boolean)

    /**
     * Установка счетчика непрочитанных диалогов и чатов.
     * @param unreadChats    количество непрочитанных чатов.
     * @param unreadDialogs  количество непрочитанных диалогов.
     * @param unviewedChats  количество непросмотренных чатов.
     * @param unviewedDialogs  количество непросмотренных диалогов.
     */
    fun setUnreadAndUnviewedCounters(
        unreadChats: Int? = null,
        unreadDialogs: Int? = null,
        unviewedChats: Int? = null,
        unviewedDialogs: Int? = null
    )

    /**
     * Сменить фильтр.
     * @param itemToDisplay фильтр.
     * @param displayFilterName флаг нужно ли отображать заголовок фильтра.
     */
    fun changeFilterByType(itemToDisplay: EntitledItem, displayFilterName: Boolean)

    /**
     * Установить режим выбора диалогов/чатов.
     * @param show включить/выключить.
     */
    fun setCheckMode(show: Boolean)

    /**
     * Установить название реестра "Диалоги" или "Чаты".
     * @param navxId id вкладки.
     */
    fun setBranchTypeTitle(navxId: NavxIdDecl)

    /**
     * Открыть выбор фильтра.
     * @param initialConfiguration стартовая конфигурация окна фильтра.
     * @param currentConfiguration текущиая конфигурация окна фильтра.
     */
    fun showFilterSelection(initialConfiguration: ConversationFilterConfiguration, currentConfiguration: ConversationFilterConfiguration)

    /**
     * Показать экран подтверждения скрытия чата из списка.
     */
    fun showHideChatConfirmation()

    /**
     * Установить видимость Fab.
     * @param visible отображать/скрыть.
     */
    fun setFabVisible(visible: Boolean)

    /**
     * Установить список релевантных персон в панель выбора персоны при поиске.
     * @param data список релевантных персон.
     */
    fun setPersonSuggestData(data: List<PersonSuggestData>)

    /**
     * Изменить видимость панели выбора контактов при поиске.
     * @param needToShow отобразить/скрыть.
     */
    fun changeContactsSearchPanelVisibility(needToShow: Boolean)

    /**
     * Выбрать персону для фильтра реестра по ней.
     * @param person выбранный контакт.
     */
    fun setSelectedPersonToFilter(person: PersonSuggestData)

    /**
     * Обновление в адаптере правил отображения некоторых View у item'ов списка.
     */
    fun updateListItemsLayoutRules(showHeaderDate: Boolean, showItemsCollages: Boolean)

    /**
     * Показать индикатор синхронизации.
     */
    fun showSyncIndicator()

    /**
     * Спрятать индикатор синхронизации.
     */
    fun hideSyncIndicator()

    /**
     * Показать индикатор отсутствия сети.
     */
    fun showNetworkWaitingIndicator(show: Boolean)

    /**
     * Показать нотификацию об ошибке во время инкрементальной синхронизации.
     */
    fun showSyncErrorNotification(isNetworkError: Boolean = true)

    /**
     * Показать нотификацию ошибки при выборе канала в шаринге,
     * если он не доступен пользователю для отправки сообщений.
     */
    fun showShareToNotAvailableChannelPopup()

    /**
     * Показать индикатор загрузки при пейджинге.
     */
    fun showPagingLoadingProgress(show: Boolean)

    /**
     * Показать ошибку загрузки при пейджинге.
     */
    fun showPagingLoadingError()

    /**
     * Сбросить состояние ошибки загрузки при пейджинге.
     */
    fun resetPagingLoadingIndicator()

    /**
     * Показать заглушку по результатам контроллера.
     * передать null чтобы спрятать заглушку.
     */
    fun showStub(stub: Stubs?)

    /**
     * Раскрыть поисковую строку под шапкой.
     */
    fun showSearchPanel()

    /**
     * Снять фокус с поисковой строки.
     */
    fun clearFocusFromSearchPanel()

    /**
     * Устанавливает контент в адаптер.
     */
    fun setContentToAdapter(list: MutableList<ConversationRegistryItem>)

    /**
     * Очистить текущий шаринг контент из интента.
     */
    fun cleanCurrentIntentSharingContent()

    /**
     * Сбросить поисковый запрос. [makeSearchRequest] - выполнить поиск после сброса или нет.
     */
    fun clearSearchQuery(makeSearchRequest: Boolean = true)

    /**
     * Показать диалоговое окно подтверждения удаления диалога.
     */
    fun showDeletingConfirmationDialog()

    /**
     * Показать диалоговое окно подтверждения удаления уведомлений по диалогу.
     */
    fun showNoticeDeletingConfirmationDialog()

    /**
     * Показать диалоговое окно подтверждения удаления своего диалога.
     */
    fun showDeletingConfirmationDialogForAll()

    /**
     * Показать диалоговое окно подтверждения удаления своего диалога.
     */
    fun showMassDeletingConfirmationDialogForAll()

    /**
     * Показать диалоговое окно подтверждения массового удаления диалогов с уведомлениями.
     */
    fun showMassDeletingDialogsAndNoticeConfirmationDialog()

    /**
     * Показать диалоговое окно для уведомления об ошибки принятия/отклонения приглашения в группу.
     */
    fun showGroupInvitedDialog(comment: String)

    /**
     * Показать сообщение о том, что переносимые диалоги уже находятся в папке, выбранной для переноса.
     * @param errorMessage Текст сообщения.
     */
    fun showDialogInFolderAlready(errorMessage: String)

    /**
     * Показать сообщение об успешном переносе диалогов/чатов в папку.
     * @param count количество успешно перенесенных диалогов/чатов.
     */
    fun showSuccessMoveToFolder(@IntRange(from = 1, to = 2) count: Int)

    /**
     * Перезапуск отложенного показа прогресса загрузки.
     */
    fun restartProgress()

    /**
     * Обновить данные в списке.
     */
    fun updateDataList(oldList: List<ConversationRegistryItem>?, newList: List<ConversationRegistryItem>?, offset: Int, forceNotifyDataSetChanged: Boolean)

    /**
     * Признак состояния скроллирования реестра в текущий момент.
     */
    val isScrolling: Boolean

    /**
     * Признак конфигурации планшета.
     */
    val isTablet: Boolean

    /**
     * Уведомить о результате выбора диалога/канала слушателя для шаринга.
     */
    fun notifyShareSelectionListener(conversationModel: ConversationModel)

    /**
     * Проверить нужно ли запросить разрешение на чтение контактов с устройства.
     */
    fun shouldRequestContactsPermissions(): Boolean

    /**
     * Импортировать контакты с проверкой разрешения на это действие.
     */
    fun importContactsSafe()

    /**
     * Сменить вкладку диалоги/каналы при помощи TabsView.
     */
    fun changeTabSelection(navxId: NavxId)

    /**
     * Изменить внешний вид шапки исходя из доступных вкладок.
     */
    fun changeHeaderByTabsAvailabilityChanges(
        dialogsAvailable: Boolean,
        channelsAvailable: Boolean
    )

    /**
     * Показать диалог для жалобы.
     */
    fun showComplainDialogFragment(complainUseCase: ComplainUseCase)
}