package ru.tensor.sbis.message_panel.attachments

import ru.tensor.sbis.disk.decl.params.DiskDocumentParams

/**
 * Обработчик события выбора файлов
 *
 * @author vv.chekurda
 */
internal class MessagePanelAttachmentSelectionHandler(private val attachmentPresenter: MessagePanelAttachmentHelper) {

    @JvmName("FilesPicker_onSelected")
    fun onSelected(uris: List<String>, diskParams: List<DiskDocumentParams>, compressImages: Boolean = false) {
        if (uris.isNotEmpty()) {
            attachmentPresenter.addAttachments(uris, compressImages)
        }
        if (diskParams.isNotEmpty()) {
            attachmentPresenter.addDiskAttachments(diskParams)
        }
    }
}