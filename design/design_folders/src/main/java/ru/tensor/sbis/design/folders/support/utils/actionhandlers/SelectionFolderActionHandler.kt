package ru.tensor.sbis.design.folders.support.utils.actionhandlers

import ru.tensor.sbis.design.folders.data.FolderActionHandler
import ru.tensor.sbis.design.folders.data.model.FolderActionType
import ru.tensor.sbis.design.folders.support.FoldersViewModel
import ru.tensor.sbis.design.folders.support.extensions.getFolder
import ru.tensor.sbis.design.folders.support.listeners.FolderActionListener

/**
 * @author ma.kolpakov
 */
internal class SelectionFolderActionHandler(
    private val foldersViewModel: FoldersViewModel,
    private val actionsListener: FolderActionListener
) : FolderActionHandler {

    override fun handleAction(actionType: FolderActionType, folderId: String, folderName: String?) {
        require(actionType == FolderActionType.CLICK) {
            "Unexpected action type for folders in selection mode $actionType"
        }
        actionsListener.selected(foldersViewModel.getFolder(folderId))
    }
}