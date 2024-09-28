package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.information

import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.mvp.presenter.BaseTwoWayPaginationPresenter
import ru.tensor.sbis.mvp.presenter.BaseTwoWayPaginationView
import java.util.UUID

/**
 * Контракт для View, Presenter информации о диалоге
 *
 * @author da.zhukov
 */
internal interface DialogInformationContract {

    interface View : BaseTwoWayPaginationView<ThemeParticipantListItem> {

        /**
         * Метод устанавливает в адаптер список участников
         */
        fun setParticipants(participants: List<ThemeParticipantListItem.ThemeParticipant>)

        /**
         * Закончить с результатом uuid выбранного участника
         */
        fun finishWithUuidResult(profileUuid: UUID)

        /**
         * Показать карточку сотрудника
         */
        fun openProfile(profileUuid: UUID)

        /**
         * Начать новую переписку
         */
        fun startConversation(profileUuid: UUID)

        /**
         * Начать видеовызов
         */
        fun startCall(profileUuid: UUID)

        /** @SelfDocumented */
        fun showToast(stringRes: Int)

        /** @SelfDocumented */
        fun showToast(message: String)

        /**
         * Установить название диалога
         */
        fun setDialogTitle(title: String)

        /**
         * Изменить видимость кнопки подтверждения
         */
        fun changeActionDoneButtonVisibility(isVisible: Boolean)

        /**
         * Изменить видимость кнопки очистки названия диалога
         */
        fun changeClearTitleButtonVisibility(isVisible: Boolean)

        /**
         * Закончить со строкой названия диалога
         */
        fun finishWithStringResult(dialogTitle: String)
    }

    interface Presenter : BaseTwoWayPaginationPresenter<View> {

        /** @SelfDocumented */
        fun onDoneButtonClicked()

        /** @SelfDocumented */
        fun onItemClick(profileUuid: UUID)

        /** @SelfDocumented */
        fun onItemPhotoClick(profileUuid: UUID)

        /** @SelfDocumented */
        fun onStartConversationClick(profileUuid: UUID)

        /** @SelfDocumented */
        fun onStartVideoCallClick(profileUuid: UUID)

        /** @SelfDocumented */
        fun onDialogTitleChanged(title: String)

        /**
         * Метод возвращает true, если диалог новый
         */
        fun isNewDialog(): Boolean
    }
}