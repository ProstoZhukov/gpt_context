package ru.tensor.sbis.communicator.sbis_conversation.ui.toolbar

import ru.tensor.sbis.communication_decl.complain.data.ComplainUseCase
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts.BaseConversationToolbarView
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts.BaseConversationKeyboardEvents
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts.BaseConversationToolbarPresenterContract
import ru.tensor.sbis.communicator.common.themes_registry.dialog_info.document_plate_view.DocumentPlateViewModel
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationRouterHolder
import ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option.ConversationOption

/**
 * Контракт для View и Presenter тулбара реестра сообщений
 */
internal interface ConversationToolbarContract {

    /** @SelfDocumented */
    interface View : BaseConversationToolbarView<ConversationMessage> {

        /**
         * Установить имя и текст документа
         */
        fun setDocumentPlateData(data: DocumentPlateViewModel)

        /**
         * Показать диалог подтверждения покидания чата
         */
        fun showLeaveChatConfirmationDialog()

        /** @SelfDocumented */
        fun showKeyboard()

        /** @SelfDocumented */
        fun focusMessagePanel()

        /** @SelfDocumented */
        fun focusEditTitle()

        /**
         * Показать диалог удаления чата
         */
        fun showHideChatConfirmation()

        /**
         * Удаление диалога с показом окна подтверждения удаления
         */
        fun showDeleteConversationConfirmation()

        /**
         * Показ меню для диалогов и чатов
         * @param options опции меню
         */
        fun showConversationOptionsMenu(options: List<ConversationOption>)

        /**
         * Копирование ссылки
         * @param url ссылка для копирования
         */
        fun copyLink(url: String)

        /**
         * Показать нотификацию об ошибке во время открытия документа
         */
        fun showOpenDocumentErrorNotification()

        /**
         * Спрятать заголовок диалога
         */
        fun hideTitle()

        /**
         * Изменить состояние доступности для кликов по коллажу в тулбаре.
         *
         * @param isEnabled false, если необходимо заблокировать.
         */
        fun changeToolbarCollageEnable(isEnabled: Boolean)

        /**
         * Изменить доступность свайп-бэка.
         *
         * @param isAvailable true, если свайп-бэк доступен.
         */
        fun changeSwipeBackAvailability(isAvailable: Boolean)

        /**
         * Сбросить запись аудио/видеосообщения.
         */
        fun cancelMessageRecording()

        /**
         * Показать окно подтверждения отмены записи аудио/видеосообщения.
         */
        fun showCancelRecordingConfirmationDialog()

        /**
         * Показать диалог для жалобы.
         */
        fun showComplainDialogFragment(complainUseCase: ComplainUseCase)

        /**
         * Показать диалоговое окно для смены темы диалога.
         */
        fun showDialogTopicInput(dialogTheme: String?)
    }

    /** @SelfDocumented */
    interface Presenter<VIEW : View> : BaseConversationToolbarPresenterContract<VIEW>,
        ConversationRouterHolder,
        BaseConversationKeyboardEvents {

        /**
         * Открытие документа по которому идет обсуждение
         */
        fun openDocument()

        /**
         * Показать меню переписки
         */
        fun onToolbarMenuIconClicked()

        /**
         * Покинуть и удалить чат
         */
        fun onQuitAndHideChatConfirmed()

        /**
         * Покинуть чат
         */
        fun onQuitChatConfirmed()

        /**
         * Удалить чат
         */
        fun onHideChatConfirmed()

        /**
         * Обработка выбранного элемента меню переписки
         */
        fun onConversationOptionSelected(conversationOption: ConversationOption)

        /**
         * Восстановление меню после смены ориентации
         */
        fun restorePopupMenuVisibility(wasVisible: Boolean)

        /**
         * Обработать результат диалога подтверждения отмены записи аудио/видеосообщения.
         */
        fun onCancelRecordingDialogResult(isConfirmed: Boolean)

        /** @SelfDocumented */
        fun onBackPressed(): Boolean

        /**
         * Обработать нажатие на коллаж/фото в тулбаре в сценарии диалога 1 на 1.
         */
        fun onTitlePhotoClick()

        /**
         * Установить тему диалога.
         */
        fun setDialogTitle(text: String)

        /**
         * Обработать нажатие на кнопку подтверждения редактирования заголовка.
         */
        fun onCompleteTitleEditClicked(newTitle: CharSequence)

        /**
         * Выполнить действия до начала обработки клик по шапке.
         */
        fun beforeToolbarClick()

        /**
         * Выполнить действия по нажатию на заголовок шапки.
         */
        fun onTitleTextClick()
    }
}