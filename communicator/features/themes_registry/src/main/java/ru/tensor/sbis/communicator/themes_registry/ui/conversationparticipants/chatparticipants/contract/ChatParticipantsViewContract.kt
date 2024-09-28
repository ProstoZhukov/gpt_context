package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.contract

import androidx.annotation.StringRes
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.mvp.search.SearchablePresenter
import ru.tensor.sbis.mvp.search.SearchableView
import java.util.UUID

/**
 * Контракт для View и Presenter участников чата
 *
 * @author rv.krohalev
 */
internal interface ChatParticipantsViewContract {

    /** @SelfDocumented */
    interface View : SearchableView<ThemeParticipantListItem> {

        /** @SelfDocumented */
        fun openProfile(profileUuid: UUID)

        /** @SelfDocumented */
        fun showRecipientsSelection(uuid: UUID)

        /** @SelfDocumented */
        fun finishWithUuidResult(profileUuid: UUID)

        /** @SelfDocumented */
        fun showToast(@StringRes message: Int)

        /** @SelfDocumented */
        fun onFolderClick(folder: ThemeParticipantListItem.ThemeParticipantFolder)
    }

    /** @SelfDocumented */
    interface Presenter : SearchablePresenter<View> {

        /** @SelfDocumented */
        fun onAddClick()

        /** @SelfDocumented */
        fun onItemClick(profileUuid: UUID)

        /** @SelfDocumented */
        fun onItemPhotoClick(profileUuid: UUID)

        /** @SelfDocumented */
        fun onChangeAdminStatusClick(chatParticipant: ThemeParticipantListItem.ThemeParticipant)

        /**
         * Действие по удалению участника из чата
         *
         * @param uuid - uuid исключаемого участника чата
         * @param isByDismiss - true если удалён смахиванием, false если по нажатию на кнопку в свайп-меню
         */
        fun onRemoveParticipantClick(uuid: UUID, isByDismiss: Boolean)

        /** @SelfDocumented */
        fun onFolderClick(folder: ThemeParticipantListItem.ThemeParticipantFolder)
    }
}