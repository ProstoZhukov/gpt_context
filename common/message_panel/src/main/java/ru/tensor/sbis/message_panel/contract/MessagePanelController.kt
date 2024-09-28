package ru.tensor.sbis.message_panel.contract

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.tensor.sbis.attachments.generated.FileInfo
import ru.tensor.sbis.common.util.illegalState
import ru.tensor.sbis.communicator.generated.SignActions
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerEvent
import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem
import ru.tensor.sbis.disk.decl.params.DiskDocumentParams
import ru.tensor.sbis.message_panel.attachments.MessagePanelAttachmentHelper
import ru.tensor.sbis.message_panel.attachments.MessagePanelAttachmentSelectionHandler
import ru.tensor.sbis.message_panel.delegate.MessagePanelFilesPickerConfig
import ru.tensor.sbis.message_panel.di.MessagePanelComponentProvider
import ru.tensor.sbis.message_panel.helper.media.MediaRecordData
import ru.tensor.sbis.message_panel.interactor.attachments.MessagePanelAttachmentsInteractor
import ru.tensor.sbis.message_panel.interactor.recipients.MessagePanelRecipientsInteractor
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import ru.tensor.sbis.message_panel.model.EditContent
import ru.tensor.sbis.message_panel.model.QuoteContent
import ru.tensor.sbis.message_panel.model.ShareContent
import ru.tensor.sbis.message_panel.view.AttachmentsDelegate
import ru.tensor.sbis.message_panel.view.AttachmentsDelegateImpl
import ru.tensor.sbis.message_panel.viewModel.MessageAttachError
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModelFactory
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModelImpl
import ru.tensor.sbis.message_panel.viewModel.livedata.hint.MessagePanelHintConfig
import ru.tensor.sbis.message_panel.viewModel.stateMachine.CleanSendState
import ru.tensor.sbis.message_panel.viewModel.stateMachine.SimpleSendState
import ru.tensor.sbis.persons.IContactVM
import ru.tensor.sbis.recorder.decl.RecorderView
import ru.tensor.sbis.recorder.decl.RecorderViewDependency
import java.util.UUID

typealias MessageSendingListener = () -> Unit
typealias MessageSentListener<MESSAGE_SENT_RESULT> = (MESSAGE_SENT_RESULT) -> Unit
typealias FocusChangeListener = (Boolean) -> Unit
typealias MessageEditListener<MESSAGE_RESULT> = (MESSAGE_RESULT) -> Unit
typealias MessageEditCancelListener = () -> Unit
typealias MessageAttachmentErrorClickListener = (MessageAttachError) -> Unit
typealias RecipientsListener = (recipients: List<IContactVM>, selectedByUser: Boolean) -> Unit
typealias AttachmentListListener = (attachments: List<FileInfo>) -> Unit
typealias TextChangeListener = (text: String) -> Unit
typealias OnKeyboardForcedHiddenListener = () -> Unit

/**
 * Слушателей запроса выбора получателей
 * Удалить после https://online.sbis.ru/doc/83240201-d85d-4fa1-aa2f-3c1f1287904f
 *
 * @author ra.stepanov
 */
interface MessageCustomRecipientListener {

    /**
     * Отобразить диалоговое окно выбора получателей
     */
    fun showRecipientSelectionDialog()

    /**
     * Очистить результат выбора получателей
     */
    fun clearRecipientSelectionResult()
}

/**
 * Интерфейс зависимостей для связывания модели панели сообщений с компонентом использующим ее.
 */
interface MessagePanelBindingDependency<MESSAGE_RESULT, MESSAGE_SENT_RESULT> {

    /** Менеджер дочерних фрагментов */
    val childFragmentManager: FragmentManager

    /** Необходимые для [RecorderView] зависимости */
    val recorderDependency: RecorderViewDependency?

    /** Обработчик подписания документов из панели ввода */
    val signDelegate: MessagePanelSignDelegate?

    /** Объект для взаимодействия с компонентом выбора файлов */
    val attachmentsDelegate: AttachmentsDelegate

    /** Подписка на отправку сообщения из панели */
    var onMessageSending: MessageSendingListener?

    /**
     * Слушатель для выбора получателей на прикладной стороне
     * Удалить после https://online.sbis.ru/doc/83240201-d85d-4fa1-aa2f-3c1f1287904f
     */
    var customRecipientListener: MessageCustomRecipientListener?

    /** Подписка на завершение отправки сообщения из панели */
    var onMessageSent: MessageSentListener<MESSAGE_SENT_RESULT>?

    /** Подписка на отправку отредактированного сообщения */
    var onMessageEdit: MessageEditListener<MESSAGE_RESULT>?

    /** Подписка на сброс редактирования сообщения */
    var onMessageEditCanceled: MessageEditCancelListener?

    /** Подписка на клик по иконке ошибки загрузки вложения */
    var onMessageAttachmentErrorClicked: MessageAttachmentErrorClickListener?

    /** Подписка на изменение фокуса на поле ввода */
    var onFocusChanged: FocusChangeListener?

    /**
     * Подписка на изменение списка получателей. Список получателей можно использовать только в информационных целях.
     * Изменение списка или его элементов приведёт к неопределённым последствиям
     */
    var onRecipientsChanged: RecipientsListener?

    /**
     * Подписка на изменение списка вложений, которые добавлены в панель ввода
     */
    var onAttachmentListChanged: AttachmentListListener?

    /**
     * Подписка на изменение текста в панели ввода
     */
    var onTextChanged: TextChangeListener?

    /**
     * Слушатель насильного закрытия клавиатуры из панели сообщений.
     */
    var onKeyboardForcedHidden: OnKeyboardForcedHiddenListener?
}

/**
 * Основной класс для управления панелью сообщений
 * @author Subbotenko Dmitry
 */
interface MessagePanelController<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT> :
    MessagePanelBindingDependency<MESSAGE_RESULT, MESSAGE_SENT_RESULT> {
    val attachmentPresenter: MessagePanelAttachmentHelper

    val viewModel: MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>

    /**
     * Переводит панель в режим редактирования сообщений.
     * @param editingMessage UUID редактируемого сообщения
     */
    fun editMessage(editingMessage: UUID)

    /**
     * Переводит панель в режим редактирования сообщений.
     * @see EditContent
     */
    fun editMessage(content: EditContent)

    /**
     * Переводит панель в режим ответа с цитированием сообщения
     */
    fun quoteMessage(content: QuoteContent, showKeyboard: Boolean = true)

    /**
     * Установка контента, которым нужно поделиться. Вызов переопределяет пользовательский ввод данными из модели
     */
    fun shareMessage(content: ShareContent)

    /**
     * Отправляет сообщение с меткой подписи [action]. Правила разрешения отправки сообщения с подписью должны быть
     * реализованы на стороне пользователя
     */
    fun signMessage(action: SignActions)

    /**
     * Отмена редактирования и перевод панели в обычный режим
     *
     * @param editingMessage параметр для условной отмены: реадиктирование будет отменено, если
     * *uuid* редактируемого сообщения совпадает с [editingMessage] иначе вызов не окажет влияния
     */
    fun cancelEdit(editingMessage: UUID? = null)

    /**
     * Переводит панель в режим ответа на сообщение
     *
     * @param conversationUuid UUID диалога
     * @param documentUuid UUID документа, новости
     * @param messageUuid UUID сообщения, на которое публикуется ответ
     */
    fun replyComment(conversationUuid: UUID, messageUuid: UUID, documentUuid: UUID, showKeyboard: Boolean = true)

    /**
     * Установка параметров панели сообщений.
     * @param coreConversationInfo дата класс, содержащий основные параметры компонента
     */
    fun setConversationInfo(coreConversationInfo: CoreConversationInfo?)

    /**
     * Метод установки получателей. Установка получателей работает до тех пор, пока панель ввода находится в состоянии
     * [CleanSendState]. При откате в первоначальное состояние `CleanSendState(needToClean = true)` будет
     * использоваться список получателей из [CoreConversationInfo.recipients]
     * @param isUserSelected передавать true, если получатели были выбраны пользователем вручную,
     * в ином случае находясь в состоянии [SimpleSendState] подстановка получателей, которые не были выбраны пользователем,
     * будут блокироваться панелью
     */
    fun setRecipients(recipients: List<UUID>, isUserSelected: Boolean = false)

    /**
     * Добавить список получателей к существующим выбранным.
     * @see setRecipients
     */
    fun addRecipients(recipients: List<UUID>, isUserSelected: Boolean = false)

    /**
     * Лисенер пермишенов. Необходимо вызывать из соответствующего метода активити или фрагмента.
     */
    fun onPermissionsGranted(grantedPermissionsList: List<String>)

    /**
     * Активация режима отображения "Ввод нового сообщения"
     */
    fun setNewDialogModeEnabled(enabled: Boolean)

    /**
     * Установка конфигурации хинта панели ввода
     */
    fun updateHintConfig(hintConfig: MessagePanelHintConfig)

    /**
     * Запрос на выбор получателей
     */
    fun requestRecipientsSelection()

    /**
     * Запрос отправки сообщения по прикладным правилам. Стандартные ограничения панели ввода игнорируются
     */
    fun sendMessage()

    /**
     * Отправить аудио/видео сообщение.
     *
     * @param data данные записи аудио/видео сообщения.
     */
    fun sendMediaMessage(data: MediaRecordData)

    /**
     * Изменить эмоцию отправленного медиа сообщения.
     */
    fun editMediaMessageEmotion(emotionCode: Int)

    /**
     * Начать перезагрузку прикрепленного вложения.
     */
    fun restartUploadAttachment(id: Long)

    /**
     * Изменить видимость панели получателей.
     */
    fun changeRecipientsViewVisibility(isVisible: Boolean)
}

open class MessagePanelControllerImpl<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(
    private val fragment: Fragment,
    context: Context,
    messageServiceDependency: MessageServiceDependency<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>,
    recipientsInteractor: MessagePanelRecipientsInteractor?,
    attachmentsInteractor: MessagePanelAttachmentsInteractor,
    override val recorderDependency: RecorderViewDependency? = null,
    override val signDelegate: MessagePanelSignDelegate? = null
) : MessagePanelController<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT> {

    private val messagePanelDependency = MessagePanelComponentProvider[context].dependency
    private val filesPicker = messagePanelDependency.createSbisFilesPicker(fragment)
    private val messagePanelAttachmentSelectionHandler by lazy {
        MessagePanelAttachmentSelectionHandler(attachmentPresenter)
    }

    final override val attachmentPresenter get() = viewModel.attachmentPresenter

    override val childFragmentManager: FragmentManager
        get() = fragment.childFragmentManager


    override val viewModel: MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT> by lazy {
        ViewModelProvider(
            fragment,
            MessagePanelViewModelFactory(
                context.applicationContext,
                messageServiceDependency,
                recipientsInteractor,
                attachmentsInteractor
            )
        ).get(MessagePanelViewModelImpl::class.java)
            as MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>
    }

    final override val attachmentsDelegate: AttachmentsDelegate by lazy(LazyThreadSafetyMode.NONE) {
        // ленивая инициализация т.к. viewModel переопределяется
        AttachmentsDelegateImpl(fragment, filesPicker)
    }

    override var onMessageSending: MessageSendingListener? = null
    override var onMessageSent: MessageSentListener<MESSAGE_SENT_RESULT>? = null
    override var onMessageEdit: MessageEditListener<MESSAGE_RESULT>? = null
    override var onMessageEditCanceled: MessageEditCancelListener? = null
    override var onMessageAttachmentErrorClicked: MessageAttachmentErrorClickListener? = null
    override var onFocusChanged: FocusChangeListener? = null
    override var onRecipientsChanged: RecipientsListener? = null
    override var onAttachmentListChanged: AttachmentListListener? = null
    override var onTextChanged: TextChangeListener? = null
    override var onKeyboardForcedHidden: OnKeyboardForcedHiddenListener? = null
    override var customRecipientListener: MessageCustomRecipientListener? = null

    override fun setConversationInfo(coreConversationInfo: CoreConversationInfo?) {
        if (coreConversationInfo == null) {
            return disable()
        }
        if (viewModel.setConversationInfo(coreConversationInfo)) {
            if (!coreConversationInfo.saveDraftMessage || !viewModel.loadDraft()) viewModel.enable()
        }
    }

    override fun setRecipients(recipients: List<UUID>, isUserSelected: Boolean) =
        viewModel.setRecipients(recipients, isUserSelected)

    override fun addRecipients(recipients: List<UUID>, isUserSelected: Boolean) =
        viewModel.addRecipients(recipients, isUserSelected)

    private fun disable() {
        viewModel.setConversationInfo(CoreConversationInfo())
        viewModel.disable()
    }

    override fun editMessage(editingMessage: UUID) = viewModel.editMessage(EditContent(uuid = editingMessage))
    override fun editMessage(content: EditContent) = viewModel.editMessage(content)
    override fun quoteMessage(content: QuoteContent, showKeyboard: Boolean) =
        viewModel.quoteMessage(content, showKeyboard)

    override fun shareMessage(content: ShareContent) = viewModel.shareMessage(content)
    override fun signMessage(action: SignActions) = viewModel.signMessage(action)

    override fun cancelEdit(editingMessage: UUID?) = viewModel.cancelEdit(editingMessage)

    override fun replyComment(conversationUuid: UUID, messageUuid: UUID, documentUuid: UUID, showKeyboard: Boolean) =
        viewModel.replyComment(conversationUuid, messageUuid, documentUuid, showKeyboard)

    override fun onPermissionsGranted(grantedPermissionsList: List<String>) = Unit

    /**
     * Метод для реакций на сохранение состояния. Не является частью публичного API
     */
    fun onSaveInstanceState(outState: Bundle) = viewModel.onSaveInstanceState()

    override fun setNewDialogModeEnabled(enabled: Boolean) = viewModel.liveData.newDialogModeEnabled(enabled)

    override fun updateHintConfig(hintConfig: MessagePanelHintConfig) {
        viewModel.liveData.updateHintConfig(hintConfig)
    }

    override fun requestRecipientsSelection() = viewModel.liveData.onRecipientButtonClick()

    override fun sendMessage() = viewModel.sendMessage()

    override fun sendMediaMessage(data: MediaRecordData) {
        viewModel.sendMediaMessage(data)
    }

    override fun editMediaMessageEmotion(emotionCode: Int) {
        viewModel.editMediaMessageEmotion(emotionCode)
    }

    override fun restartUploadAttachment(id: Long) {
        viewModel.restartUploadAttachment(id)
    }

    override fun changeRecipientsViewVisibility(isVisible: Boolean) {
        viewModel.liveData.forceHideRecipientsPanel(!isVisible)
    }

    /** @SelfDocumented */
    internal fun initAttachmentsDelegate(filesPickerConfig: MessagePanelFilesPickerConfig) {
        attachmentsDelegate.filesPickerConfig = filesPickerConfig
        fragment.lifecycleScope.launch {
            filesPicker.events.collect {
                if (it is SbisFilesPickerEvent.OnItemsSelected) {
                    if (it.selectedItems.isEmpty()) return@collect
                    val diskFiles = mutableListOf<DiskDocumentParams>()
                    val localFileUris = mutableListOf<String>()
                    val nonFilesUris = mutableListOf<String>()
                    for (unit in it.selectedItems) {
                        when (unit) {
                            is SbisPickedItem.DiskDocument -> diskFiles.add(unit.params)
                            is SbisPickedItem.LocalFile -> localFileUris.add(unit.uri)
                            is SbisPickedItem.Uri -> nonFilesUris.add(unit.url)
                            is SbisPickedItem.Barcode -> illegalState { "Unexpected file type ${unit::class.java}" }
                        }
                    }
                    when {
                        localFileUris.isNotEmpty() || diskFiles.isNotEmpty() -> {
                            messagePanelAttachmentSelectionHandler.onSelected(
                                localFileUris,
                                diskFiles,
                                it.compressImages
                            )
                        }
                        nonFilesUris.size == 1 -> {
                            val url = nonFilesUris.first()
                            viewModel.onPickerLinkSelected(url)
                        }
                        nonFilesUris.isNotEmpty() -> {
                            val builder = StringBuilder()
                            for (uri in nonFilesUris) builder.append(uri)
                            viewModel.liveData.concatMessageText(builder.toString())
                        }
                    }
                }
            }
        }
    }
}

