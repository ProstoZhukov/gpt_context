package ru.tensor.sbis.communicator.communicator_files.ui

import ru.tensor.sbis.communication_decl.conversation_information.ConversationInformationFilterableContent
import ru.tensor.sbis.communication_decl.conversation_information.ConversationInformationSearchableContent
import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem

/**
 * Интерфейс для раздела "Файлы" экрана информации диалога/канала.
 *
 * @author da.zhukov
 */
interface ConversationInformationFilesContent : ConversationInformationFilterableContent,
    ConversationInformationSearchableContent,
    ConversationInformationContentWithFolders {

    /** Добавить файлы. */
    fun addFiles(selectedFiles: List<SbisPickedItem>, compressImages: Boolean)
}

/**
 * Интерфейс для раздела "Файлы" экрана информации диалога/канала, который поддерживает создание папки.
 *
 * @author da.zhukov
 */
interface ConversationInformationContentWithFolders {

    /** Создать папку. */
    fun createFolder(folderName: String)
}