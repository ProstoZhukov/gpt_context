package ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.vm.live_data

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.data.SelectionPersonItem
import java.util.UUID

/**
 * Live-data раздела шаринга в контакты для подписки на состояние и события экрана.
 *
 * @author vv.chekurda
 */
internal interface ContactsShareViewLiveData {

    /**
     * Идентификатор переписки.
     */
    val conversationUuid: StateFlow<UUID?>

    /**
     * Выбранная персона.
     */
    val selectedPerson: StateFlow<SelectionPersonItem?>

    /**
     * Список выбранных персон для панели.
     */
    val multiSelectedItems: StateFlow<List<SelectionItem>>

    /**
     * Видимость панели сообщений.
     */
    val messagePanelVisibility: StateFlow<Boolean>

    /**
     * Видимость панели выбранных получателей.
     */
    val selectionPanelVisibility: StateFlow<Boolean>

    /**
     * Видимость компонента выбора получателей.
     */
    val recipientSelectionVisibility: StateFlow<Int>

    /**
     * Событие для подъема клавиатуры.
     */
    val showKeyboard: SharedFlow<Unit>

    /**
     * Событие для опускания клавиатуры.
     */
    val hideKeyboard: SharedFlow<Unit>

    /**
     * Текст отправляемого сообщения.
     */
    val sendingMessage: StateFlow<String>

    /**
     * Нижний отступ для списка.
     */
    val bottomOffset: StateFlow<Int>
}