package ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.vm.live_data

import android.view.View
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.vm.data.SendContactsShareData
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.data.SelectionPersonItem
import ru.tensor.sbis.toolbox_decl.share.ShareData
import ru.tensor.sbis.toolbox_decl.share.content.data.ShareMenuHeightMode
import java.util.UUID

/**
 * Реализаци live-data раздела шаринга в контакты для подписки на состояния и события экрана.
 *
 * @author vv.chekurda
 */
internal class ContactsShareLiveData : ContactsShareViewLiveData {

    private val _messagePanelText = MutableStateFlow<CharSequence>(StringUtils.EMPTY)
    private val _sendShareData = MutableSharedFlow<SendContactsShareData>(extraBufferCapacity = 1)
    private val _finishShare = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val _unselectItem = MutableSharedFlow<SelectionItem>(extraBufferCapacity = 1)
    private val _changeHeightMode = MutableSharedFlow<ShareMenuHeightMode>(extraBufferCapacity = 1)
    private val _menuBackButtonVisibility = MutableSharedFlow<Boolean>(replay = 1)
    private val _menuNavPanelVisibility = MutableSharedFlow<Boolean>(replay = 1)
    private val _loadSelectedPersonData = MutableSharedFlow<UUID>(replay = 1)
    private val _createDraftDialog = MutableSharedFlow<ShareData>(replay = 1)

    override val conversationUuid = MutableStateFlow<UUID?>(null)
    override val selectedPerson = MutableStateFlow<SelectionPersonItem?>(null)
    override val multiSelectedItems = MutableStateFlow(emptyList<SelectionItem>())
    override val messagePanelVisibility = MutableStateFlow(false)
    override val selectionPanelVisibility = MutableStateFlow(false)
    override val recipientSelectionVisibility = MutableStateFlow(View.VISIBLE)
    override val showKeyboard = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val hideKeyboard = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val sendingMessage  = MutableStateFlow(StringUtils.EMPTY)
    override val bottomOffset = MutableStateFlow(0)

    /**
     * Событие для отправки сообщения с данными [SendContactsShareData].
     */
    val sendShareData: Flow<SendContactsShareData> = _sendShareData

    /**
     * Событие ддля загрузки модели персоны по идентификатору.
     */
    val loadSelectedPersonData: Flow<UUID> = _loadSelectedPersonData

    /**
     * Событие для создания драфтового диалога для данных [ShareData].
     */
    val createDraftDialog: Flow<ShareData> = _createDraftDialog

    /**
     * Событие для завершения шаринга.
     */
    val finishShare: Flow<Unit> = _finishShare

    /**
     * Событие для отмены выбора получателя [SelectionItem].
     */
    val unselectItem: Flow<SelectionItem> = _unselectItem

    /**
     * Событие для изменения мода высоты меню.
     */
    val changeHeightMode: Flow<ShareMenuHeightMode> = _changeHeightMode

    /**
     * Видимость панели навигации меню.
     */
    val menuNavPanelVisibility: Flow<Boolean> = _menuNavPanelVisibility

    /**
     * Видимость кнопки назад в шапке меню.
     */
    val menuBackButtonVisibility: Flow<Boolean> = _menuBackButtonVisibility

    /**
     * Текст, набранный пользователем в панели сообщений.
     */
    val messagePanelText: StateFlow<CharSequence> = _messagePanelText

    /**
     * Установить идентификатор переписки.
     */
    fun setConversationUuid(uuid: UUID) {
        conversationUuid.tryEmit(uuid)
    }

    /**
     * Установить выбранную персону.
     */
    fun setSelectedPerson(person: SelectionPersonItem?) {
        selectedPerson.tryEmit(person)
    }

    /**
     * Установить список выбранных персон.
     */
    fun setMultiSelectedPersons(persons: List<SelectionItem>) {
        multiSelectedItems.tryEmit(persons)
    }

    /**
     * Установить текст панели сообщений.
     */
    fun setMessagePanelText(text: CharSequence) {
        _messagePanelText.tryEmit(text)
    }

    /**
     * Изменить видимость панели сообщений.
     */
    fun changeMessagePanelVisibility(isVisible: Boolean) {
        messagePanelVisibility.tryEmit(isVisible)
    }

    /**
     * Изменить видимость панели выбранных.
     */
    fun changeSelectionPanelVisibility(isVisible: Boolean) {
        selectionPanelVisibility.tryEmit(isVisible)
    }

    /**
     * Изменить видиимость выбора получателей.
     */
    fun changeRecipientSelectionVisibility(visibility: Int) {
        recipientSelectionVisibility.tryEmit(visibility)
    }

    /**
     * Изменить видимость кнопки назад в шапке меню.
     */
    fun changeMenuBackButtonVisibility(isVisible: Boolean) {
        _menuBackButtonVisibility.tryEmit(isVisible)
    }

    /**
     * Изменить видимость панели навигации меню.
     */
    fun changeMenuNavPanelVisibility(isVisible: Boolean) {
        _menuNavPanelVisibility.tryEmit(isVisible)
    }

    /**
     * Установить данные для отправки сообщения.
     */
    fun setSendContactsShareData(data: SendContactsShareData) {
        _sendShareData.tryEmit(data)
    }

    /**
     * Установить текст отправляемого сообщения.
     */
    fun setSendingMessageText(text: String) {
        sendingMessage.tryEmit(text)
    }

    /**
     * Отменить выбор получателей [item].
     */
    fun unselectItem(item: SelectionItem) {
        _unselectItem.tryEmit(item)
    }

    /**
     * Завершить шаринг.
     */
    fun finishShare() {
        _finishShare.tryEmit(Unit)
    }

    /**
     * Изменить мод высоты меню.
     */
    fun changeHeightMode(mode: ShareMenuHeightMode) {
        _changeHeightMode.tryEmit(mode)
    }

    /**
     * Показать клавиатуру.
     */
    fun showKeyboard() {
        showKeyboard.tryEmit(Unit)
    }

    /**
     * Скрыть клавиатуру.
     */
    fun hideKeyboard() {
        hideKeyboard.tryEmit(Unit)
    }

    /**
     * Установить нижний отступ.
     */
    fun setBottomOffset(offset: Int) {
        bottomOffset.tryEmit(offset)
    }

    /**
     * Загрузить персону по идентификатору.
     */
    fun loadSelectedPersonData(personUuid: UUID) {
        _loadSelectedPersonData.tryEmit(personUuid)
    }

    /**
     * Создать драфтовый диалог.
     */
    fun createDraftDialog(shareData: ShareData) {
        _createDraftDialog.tryEmit(shareData)
    }
}