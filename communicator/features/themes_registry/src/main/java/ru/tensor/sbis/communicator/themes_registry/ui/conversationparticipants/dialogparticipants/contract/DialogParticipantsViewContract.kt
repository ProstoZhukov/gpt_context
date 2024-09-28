package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.contract

import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.mvp.search.SearchablePresenter
import ru.tensor.sbis.mvp.search.SearchableView
import java.util.UUID

/**
 * Контракт для View и Presenter участников диалога
 */
internal interface DialogParticipantsViewContract {

    /** @SelfDocumented */
    interface View : SearchableView<ThemeParticipantListItem> {

        /** @SelfDocumented */
        fun openProfile(profileUuid: UUID)

        /** @SelfDocumented */
        fun finishWithUuidResult(profileUuid: UUID)

        /**
         * Метод устанавливает в адаптер список участников
         */
        fun setParticipants(participants: List<ThemeParticipantListItem.ThemeParticipant>)

        /**
         * Начать новую переписку
         */
        fun startConversation(profileUuid: UUID)

        /**
         * Начать видеовызов
         */
        fun startCall(profileUuid: UUID)

        /**
         * Обновить отступы списка.
         */
        fun updateListPaddingsIfNeed()
    }

    /** @SelfDocumented */
    interface Presenter : SearchablePresenter<View> {

        /** @SelfDocumented */
        fun onItemClick(profileUuid: UUID)

        /** @SelfDocumented */
        fun onItemPhotoClick(profileUuid: UUID)

        /** @SelfDocumented */
        fun onStartConversationClick(profileUuid: UUID)

        /** @SelfDocumented */
        fun onStartVideoCallClick(profileUuid: UUID)

        /**
         * Метод возвращает true, если диалог новый
         */
        fun isNewDialog(): Boolean
    }
}