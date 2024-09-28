package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.participant_view

import android.view.View

/**
 * API вью единственного участника на экране информации диалога/канала.
 *
 * @author dv.baranov
 */
internal interface ConversationInformationParticipantViewAPI {

    /** Вью дата компонента. */
    var viewData: ConversationInformationParticipantViewData?

    /** Установить слушатель кликов по фото участника. */
    fun setOnPhotoClickListener(listener: View.OnClickListener?)
}