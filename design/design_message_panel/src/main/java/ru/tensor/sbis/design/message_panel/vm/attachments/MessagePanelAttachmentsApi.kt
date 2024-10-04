package ru.tensor.sbis.design.message_panel.vm.attachments

import android.view.View
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.attachments.generated.FileInfo
import ru.tensor.sbis.attachments.models.AttachmentRegisterModel
import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem
import ru.tensor.sbis.design.message_panel.decl.attachments.AttachmentUploadingProgress

/**
 * Публичный API для работы с вложениями панели ввода
 *
 * @author ma.kolpakov
 */
interface MessagePanelAttachmentsApi {

    /**
     * Модели микросервиса вложений
     */
    val attachments: StateFlow<List<FileInfo>>

    /**
     * Модели влложений для отображения пользователю
     */
    val viewAttachments: StateFlow<List<AttachmentRegisterModel>>

    /**
     * Подписка на обновление прогресса загрузки вложений
     */
    val progressAttachments: Flow<AttachmentUploadingProgress>

    /**
     * Подписка на запрос вложений (через компонент "Панель выбора файлов")
     */
    val attachmentsSelectionRequest: Flow<AttachmentsSelectionRequest>

    /**
     * Подписка на видимость кнопки прикрепления вложений
     *
     * @see onAttachButtonClicked
     */
    val attachmentButtonVisible: StateFlow<Boolean>

    /**
     * Добавляет вложения в панель ввода
     */
    fun addAttachments(attachments: List<SbisPickedItem>, isNeedCompressImages: Boolean)

    /**
     * Запрос прикрепления вложений. Параметр [anchor] передаётся в подписку
     * [attachmentsSelectionRequest], не сохраняется
     */
    fun onAttachButtonClicked(anchor: View)

    /**
     * Удаляет все вложения из панели ввода
     */
    fun onAttachmentsClearClicked()
}
