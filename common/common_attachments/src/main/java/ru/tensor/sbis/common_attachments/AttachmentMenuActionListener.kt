package ru.tensor.sbis.common_attachments

import android.net.Uri
import ru.tensor.sbis.disk.decl.select.SelectedDiskDocuments

/**
 * Обработчик событий меню прикрепления файлов
 *
 * @author us.bessonov
 */
interface AttachmentMenuActionListener {

    /**
     * Вызывается при выборе пункта меню
     *
     * @param mediaOptionValue индекс выбранного пункта
     */
    fun onAttachmentMenuItemClick(mediaOptionValue: Int)

    /**
     * Вызывается при выборе файлов
     *
     * @param files список строковых [Uri] файлов
     */
    fun onFilesInAttachmentMenuSelected(files: List<String>)

    /**
     * Вызывается при выборе документов из Диска
     *
     * @param documents список выбранных документов с указанием реестра-источника
     */
    fun onDocumentsInAttachmentMenuSelected(documents: SelectedDiskDocuments)
}