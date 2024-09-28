package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder

import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.communicator.generated.ChatNotificationOptions
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.model.ChatSettingsEditChatNameData
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.ChatSettingsParticipationTypeOptions
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.ChatSettingsTypeOptions
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipant
import java.util.UUID

/**
 * Составная часть экрана настроек канала.
 *
 * @author dv.baranov
 */
internal sealed class ChatSettingsItem {
    /**
     * Тип вью элемента списка.
     */
    abstract val viewTypeId: Int
}

/**
 * Верхняя часть экрана настроек канала.
 * @param avatarUrl ссылка на фото аватарки чата.
 * @param onAvatarViewClick действие по нажатию на аватарку.
 * @param onAvatarViewLongClick действие на долгое нажатие на аватарку.
 * @param chatSettingsEditChatNameData модель данных для работы с полем ввода названия чата.
 * @param personListTitleViewTextRes ресурс текста заголовка списка контактов.
 * @param onAddButtonClick действие по нажатию на кнопку добавления контактов.
 * @param isAddButtonVisible true, если видна кнопка добавления контактов.
 * @param onRecycleHeaderItem действие при исчезновении headerview с экрана.
 *
 * @author dv.baranov
 */
internal data class ChatSettingsHeaderItem(
    val avatarUrl: String? = null,
    val onAvatarViewClick: (() -> Unit) = {},
    val onAvatarViewLongClick: (() -> Unit) = {},
    val chatSettingsEditChatNameData: ChatSettingsEditChatNameData = ChatSettingsEditChatNameData(),
    val personListTitleViewTextRes: Int = R.string.communicator_chat_participants,
    val onAddButtonClick: (() -> Unit) = {},
    val isAddButtonVisible: Boolean = false,
    val onRecycleHeaderItem: (() -> Unit) = {},
) : ChatSettingsItem() {
    override val viewTypeId: Int = COMMUNICATOR_CHAT_SETTINGS_HEADER_ITEM
}

/**
 * Единичный контакт списка контактов экрана настроек канала.
 * @param participant модель контакта.
 * @param onItemClick действие по нажатию на item.
 * @param onRemoveAdminClick действие по удалению администратора чата.
 * @param isSwipeEnabled true, если свайп-меню должны быть доступны.
 *
 * @author dv.baranov
 */
internal data class ChatSettingsContactItem(
    val participant: ThemeParticipant,
    val onItemClick: (profileUuid: UUID) -> Unit = {},
    val onRemoveAdminClick: (admin: ThemeParticipant) -> Unit = {},
    val isSwipeEnabled: Boolean = false,
) : ChatSettingsItem() {
    override val viewTypeId: Int = COMMUNICATOR_CHAT_SETTINGS_CONTACT_ITEM
}

/**
 * Нижняя часть экрана настроек канала.
 * @param onCollapseButtonClick действие на нажатие кнопки сворачивания/разворачивания списка контактов.
 * @param collapseButtonTextResId ресурс текста кнопки сворачивания/разворачивания списка контактов.
 * @param isCollapseButtonVisible true, если нужно отобразить кнопку сворачивания/разворачивания списка контактов.
 * @param onChangeChatTypeButtonClick действие на нажатие кнопки типа чата.
 * @param onChangeParticipationTypeButtonClick действие на нажатие кнопки типа участия в чате.
 * @param chatType тип чата.
 * @param participationType тип участия в чате.
 * @param chatNotificationOptions значения чекбоксов уведомлений.
 * @param changeNotificationOptions действие на изменения значений чекбоксов.
 * @param changeActionDoneButtonVisibility изменить отображение галочки в тулбаре для сохранения изменений.
 * @param onCloseChannelButtonClick действие на клик по кнопке закрытия чата.
 * @param isCloseChannelButtonVisible видна ли кнопка закрытия чата.
 * @param skipSwitchAnimation пропустить анимацию переключения тумблера.
 * @param isNewChat true, если экран настроек нового чата, false иначе.
 * @param onRecycleFooterItem действие при исчезновении footerview с экрана.
 *
 * @author dv.baranov
 */
internal data class ChatSettingsFooterItem(
    val onCollapseButtonClick: (() -> Unit) = {},
    val collapseButtonTextResId: Int = R.string.communicator_chat_show_all,
    val isCollapseButtonVisible: Boolean = true,
    val onChangeChatTypeButtonClick: (() -> Unit) = {},
    val onChangeParticipationTypeButtonClick: (() -> Unit) = {},
    val chatType: ChatSettingsTypeOptions = ChatSettingsTypeOptions.OPEN,
    val participationType: ChatSettingsParticipationTypeOptions = ChatSettingsParticipationTypeOptions.FOR_ALL,
    val chatNotificationOptions: ChatNotificationOptions = ChatNotificationOptions(),
    val changeNotificationOptions: ((options: ChatNotificationOptions) -> Unit) = {},
    val changeActionDoneButtonVisibility: ((isVisible: Boolean) -> Unit) = {},
    val onCloseChannelButtonClick: (() -> Unit) = {},
    val isCloseChannelButtonVisible: Boolean = false,
    val skipSwitchAnimation: Boolean = false,
    val isNewChat: Boolean = false,
    val onRecycleFooterItem: (() -> Unit) = {},
) : ChatSettingsItem() {
    override val viewTypeId: Int = COMMUNICATOR_CHAT_SETTINGS_FOOTER_ITEM
}

private const val COMMUNICATOR_CHAT_SETTINGS_HEADER_ITEM = 0
private const val COMMUNICATOR_CHAT_SETTINGS_CONTACT_ITEM = 1
private const val COMMUNICATOR_CHAT_SETTINGS_FOOTER_ITEM = 2
