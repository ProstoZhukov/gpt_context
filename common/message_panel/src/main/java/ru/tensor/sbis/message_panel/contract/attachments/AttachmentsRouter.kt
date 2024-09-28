package ru.tensor.sbis.message_panel.contract.attachments

import ru.tensor.sbis.attachments.generated.FileInfo

/**
 * Интерфейс обработчика переходов для отображения вложений
 *
 * @author vv.chekurda
 * Создан 7/30/2019
 */
interface AttachmentsRouter {

    /**
     * Запрос на отображение карусели файлов
     * @param attachmentList     список вложений
     * @param selectedAttachment вложение из списка, на котором необходимо открыть просмотрщик
     */
    fun showViewerSlider(attachmentList: List<FileInfo>, selectedAttachment: FileInfo)
}