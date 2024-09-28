package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings

import androidx.annotation.StringRes
import io.reactivex.Observable
import io.reactivex.Single
import ru.tensor.sbis.communicator.generated.ChatNotificationOptions
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators.ChatAdministratorsSettingsCommandWrapper
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.ChatSettingsParticipationTypeOptions
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.ChatSettingsTypeOptions
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipant
import ru.tensor.sbis.mvp.presenter.BaseTwoWayPaginationPresenter
import ru.tensor.sbis.mvp.presenter.BaseTwoWayPaginationView
import java.util.UUID

/**
 * Контракт для View, Presenter и Interactor настроек чата
 */
internal interface ChatSettingsContract {

    /** @SelfDocumented */
    interface View : BaseTwoWayPaginationView<ChatSettingsItem> {

        /**
         * Открыть профиль контакта
         */
        fun openProfile(profileUuid: UUID)

        /** @SelfDocumented */
        fun showToast(stringRes: Int)

        /** @SelfDocumented */
        fun showToast(message: String)

        /** @SelfDocumented */
        fun setToolbarData(creatorName: String, newChat: Boolean, timestamp: Long)

        /**
         * Обновить фото канала
         */
        fun updateAvatar(dataString: String?)

        /**
         * Устоновить название для участников/администраторов
         */
        fun setPersonListTitle(@StringRes titleRes: Int)

        /** @SelfDocumented */
        fun showCloseChatButton(show: Boolean)

        /** @SelfDocumented */
        fun setChatNameEditable(editable: Boolean)

        /** @SelfDocumented */
        fun setChatName(name: String, needUpdate: Boolean = true)

        /** @SelfDocumented */
        fun changeAddPersonsButtonVisibility(isVisible: Boolean)

        /** @SelfDocumented */
        fun showChoosingRecipients(chatUuid: UUID?, isForAdmins: Boolean)

        /** @SelfDocumented */
        fun showProgressDialog(@StringRes textResId: Int)

        /** @SelfDocumented */
        fun showAvatarChangeDialog()

        /** @SelfDocumented */
        fun showChatAvatar(photoUrl: String)

        /** @SelfDocumented */
        fun hideProgressDialog()

        /** @SelfDocumented */
        fun finish(chatUuid: UUID)

        /** @SelfDocumented */
        fun cancel()

        /** @SelfDocumented */
        fun changeActionDoneButtonVisibility(isVisible: Boolean)

        /** @SelfDocumented */
        fun setSwipeEnabled(isEnabled: Boolean)

        /** @SelfDocumented */
        fun setEditNameViewBackgroundColor(isEditNameTextEmpty: Boolean)

        /** @SelfDocumented */
        fun updateCheckboxAndSwitch(
            options: ChatNotificationOptions,
            skipSwitchAnimation: Boolean = false,
            needUpdate: Boolean = true,
        )

        /**
         * Обработать нажатие на кнопку типа канала.
         */
        fun onChangeChatTypeClicked(
            options: List<ChatSettingsTypeOptions>,
            currentType: ChatSettingsTypeOptions,
        )

        /**
         * Обработать нажатие на кнопку типа участия канала.
         */
        fun onChangeParticipationTypeClicked(
            options: List<ChatSettingsParticipationTypeOptions>,
            currentType: ChatSettingsParticipationTypeOptions,
        )

        /**
         * Обновить состояние кнопок по изменению типов чата.
         */
        fun updateChatTypeButtonsState(
            currentType: ChatSettingsTypeOptions,
            currentParticipationType: ChatSettingsParticipationTypeOptions,
        )

        /**
         * Показать окно подтверждения для удаления лишних участников канала
         * при попытке применить настройку "только сотрудники".
         */
        fun showOnlyEmployeesTypeConfirmation()

        /** @SelfDocumented */
        fun showAvatarOptionMenu()
    }

    /** @SelfDocumented */
    interface Presenter : BaseTwoWayPaginationPresenter<View> {

        /** Доступность свайп-меню */
        var isSwipeEnabled: Boolean

        /** @SelfDocumented */
        fun onDoneButtonClicked()

        /** @SelfDocumented */
        fun updateChat()

        /** @SelfDocumented */
        fun onAddPersonButtonClicked()

        /** @SelfDocumented */
        fun onItemClick(profileUuid: UUID)

        /** @SelfDocumented */
        fun onChatNameChanged(name: String)

        /** @SelfDocumented */
        fun getChatName(): String

        /** @SelfDocumented */
        fun setDataFromMyProfile()

        /** @SelfDocumented */
        fun closeChat()

        /** @SelfDocumented */
        fun onRemoveAdminClick(admin: ThemeParticipant)

        /** @SelfDocumented */
        fun handleNewAvatar(imageUriString: String?)

        /** @SelfDocumented */
        fun changeNotificationOptions(all: Boolean, personal: Boolean, administrator: Boolean)

        /** @SelfDocumented */
        fun saveNotificationOptions()

        /** @SelfDocumented */
        fun onChangeChatTypeClicked()

        /** @SelfDocumented */
        fun onChangeParticipationTypeClicked()

        /** @SelfDocumented */
        fun onChatTypeSelected(newChatType: ChatSettingsTypeOptions)

        /** @SelfDocumented */
        fun onChatParticipationTypeSelected(newChatType: ChatSettingsParticipationTypeOptions)

        /** @SelfDocumented */
        fun onAvatarClick()

        /** @SelfDocumented */
        fun onAvatarLongClick()

        /** @SelfDocumented */
        fun handleAvatarOption(option: AvatarMenuOption)
    }

    /** @SelfDocumented */
    interface ChatParticipantsInteractor {

        val chatSettingsCommandWrapper: ChatSettingsCommandWrapper

        val chatAdministratorsCommandWrapper: ChatAdministratorsSettingsCommandWrapper

        fun observeThemeControllerUpdates(): Observable<HashMap<String, String>>

        fun getThemeParticipantList(participantsUuids: List<UUID>): Single<List<ThemeParticipant>>
    }
}
