package ru.tensor.sbis.communicator.sbis_conversation.ui.messagepanel

import androidx.annotation.StringRes
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts.BaseConversationMessagePanelView
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts.BaseConversationMessagePanelPresenterContract
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationRouterHolder
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.ui.attachments.ConversationAttachmentContract
import ru.tensor.sbis.design.message_panel.audio_recorder.view.contract.AudioRecordViewState
import ru.tensor.sbis.design.message_panel.video_recorder.view.contract.VideoRecordViewState
import java.util.*

/**
 * Контракт для View и Presenter панели сообщений
 */
internal interface ConversationMessagePanelContract {

    /** @SelfDocumented */
    interface View: BaseConversationMessagePanelView<ConversationMessage>,
        ConversationAttachmentContract.View {

        /**
         * Показать диалог подтверждения удаления диалога у всех
         */
        fun showPopupDeleteDialogForAll()

        /** @SelfDocumented */
        fun showProgressDialog(@StringRes textResId: Int, cancellable: Boolean)

        /** @SelfDocumented */
        fun hideProgressDialog()

        /** @SelfDocumented */
        fun showRecordError(@StringRes errorRes: Int)

        /**
         * Показать информационное сообщение, о невозможности записи голосового или видеосообщения.
         */
        fun showRecordInfoPopup(@StringRes infoRes: Int)

        /**
         * Показть окно подтверждения отмены записи аудио/видеосообщения.
         */
        fun showCancelRecordingConfirmationDialog()

        /**
         * Показать кнопку записи аудио/видео сообщений.
         */
        fun changeRecordEnable(isAudioEnabled: Boolean, isVideoEnabled: Boolean)
    }

    /** @SelfDocumented */
    interface Presenter<VIEW: View>: BaseConversationMessagePanelPresenterContract<VIEW>,
        ConversationRouterHolder {

        /** @SelfDocumented */
        fun onDialogParticipantChoosed(profileUuid: UUID)

        /** @SelfDocumented */
        fun onSignMenuItemClicked()

        /** @SelfDocumented */
        fun onRequestSignatureMenuItemClicked()

        /** @SelfDocumented */
        fun onSignAndRequestMenuItemClicked()

        /** @SelfDocumented */
        fun onChangeRecipientsClick()

        /** @SelfDocumented */
        fun onRecipientsChangingCanceled()

        /**
         * Удаление диалога
         */
        fun onDeleteDialog()

        /** @SelfDocumented */
        fun isRecipientSelectionClosed(): Boolean

        /** @SelfDocumented */
        fun messageFileSigningSuccess()

        /** @SelfDocumented */
        fun messageFileSigningFailure()

        /** @SelfDocumented */
        fun onBackPressed(): Boolean

        /**
         * Запись аудио или видео успешно завершена.
         */
        fun onRecordCompleted()

        /**
         * Изменилось состояние панели записи аудиосообщения.
         *
         * @param state новое состояние.
         */
        fun onAudioRecordStateChanged(state: AudioRecordViewState)

        /**
         * Изменилось состояние панели записи видеосообщения.
         *
         * @param state новое состояние.
         */
        fun onVideoRecordStateChanged(state: VideoRecordViewState)
    }
}