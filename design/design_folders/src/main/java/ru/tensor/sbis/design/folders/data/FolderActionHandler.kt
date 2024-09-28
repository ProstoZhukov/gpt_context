package ru.tensor.sbis.design.folders.data

import ru.tensor.sbis.design.folders.data.model.FolderActionType
import ru.tensor.sbis.design.folders.support.extensions.dialogs.RenameFolderDialogListenerFactory

/**
 * Обработчик действий с папками
 *
 * @see FolderActionType
 *
 * @author ma.kolpakov
 */
interface FolderActionHandler {

    /**
     * Обработка действия папки
     *
     * @param actionType тип действия
     * @param folderId уникальный id папки
     * @param folderName название папки (опционально), нужно для действия переименования [RenameFolderDialogListenerFactory]
     */
    fun handleAction(actionType: FolderActionType, folderId: String, folderName: String? = null)
}
