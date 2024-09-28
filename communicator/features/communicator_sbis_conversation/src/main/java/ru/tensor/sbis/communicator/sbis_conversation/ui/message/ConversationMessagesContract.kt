package ru.tensor.sbis.communicator.sbis_conversation.ui.message

import androidx.annotation.StringRes
import ru.tensor.sbis.communication_decl.complain.data.ComplainUseCase
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts.BaseConversationMessagesPresenterContract
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts.BaseConversationMessagesView
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.declaration.MessageListController
import ru.tensor.sbis.communicator.sbis_conversation.adapters.MessageActionsListener
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationRouterHolder
import ru.tensor.sbis.communicator.sbis_conversation.ui.attachments.ConversationAttachmentContract
import java.util.UUID

/**
 * Контракт для View и Presenter списка сообщений на crud коллекции.
 *
 * @author vv.chekruda
 */
internal interface ConversationMessagesContract {

    /** @SelfDocumented */
    interface View :
        BaseConversationMessagesView<ConversationMessage>,
        ConversationAttachmentContract.AttachmentsSigningView {

        fun showConversationMembers() = Unit

        /**
         * Отображение прогресса отклонения подписи / доступа к файлу.
         */
        fun showProgressInRejectButton(show: Boolean, messagePosition: Int)

        /**
         * Отображение прогресса предоставления доступа к файлу.
         */
        fun showProgressInAcceptButton(show: Boolean, messagePosition: Int) = Unit

        /**
         * Показать диалог удаления сообщения у меня.
         */
        fun showPopupDeleteMessageForMe()

        /**
         * Показать нотификацию о том что диалог был удален.
         */
        fun notifyDialogRemoved()

        /**
         * Показать меню опций для предоставления доступа к файлу.
         */
        fun showGrantAccessMenu(message: Message, messagePosition: Int, sender: android.view.View) = Unit

        /**
         * Показть окно подтверждения отмены записи аудио/видеосообщения.
         */
        fun showCancelRecordingConfirmationDialog()

        /**
         * Показать панель-информер с ошибкой.
         */
        fun showErrorPopup(@StringRes textId: Int, icon: String? = null) = Unit

        /**
         * Показать панель-информер с ошибкой.
         */
        fun showErrorPopup(text: String?, icon: String? = null) = Unit

        /**
         * Показать диалог для жалобы.
         */
        fun showComplainDialogFragment(complainUseCase: ComplainUseCase)

        /**
         * Получить строку из ресурса.
         */
        fun getStringRes(@StringRes stringId: Int): String

        /**
         * Показать меню с действиями над номером телефона сообщения.
         *
         * @param messageUuid идентификатор сообщения.
         * @param actions список действий с номером телефона.
         */
        fun showPhoneNumberActionsList(messageUuid: UUID?, actions: List<Int>)

        /** @SelfDocumented */
        fun finishConversationActivityWithCommonError()
    }

    /** @SelfDocumented */
    interface Presenter<VIEW : View> : BaseConversationMessagesPresenterContract<VIEW>,
        MessageListController,
        MessageActionsListener,
        ConversationRouterHolder,
        PhoneNumberSelectionItemListener,
        PhoneNumberVerificationErrorHandler,
        ConversationSingAndAcceptHandler {

        /** @SelfDocumented */
        val conversationUuid: UUID?


        /** @SelfDocumented */
        fun onChatCreatedFromDialog()


        /** @SelfDocumented */
        fun onViewerSliderClosed()

        /**
         * Открепить прикрепленное в чате сообщение
         */
        fun unpinChatMessage()

        /**
         * Закрылся экран выбора участников.
         */
        fun onParticipantsScreenClosed()

        /**
         * Выйти из экрана сообшений
         */
        fun close()
    }
}