package ru.tensor.sbis.design.folders.support.utils.actionhandlers

import android.content.Context
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.design.folders.data.FolderActionHandler
import ru.tensor.sbis.design.folders.data.model.FolderActionType
import ru.tensor.sbis.design.folders.support.FoldersViewModel
import ru.tensor.sbis.design.folders.support.extensions.dialogs.showCreateDialog
import ru.tensor.sbis.design.folders.support.extensions.dialogs.showDeleteDialog
import ru.tensor.sbis.design.folders.support.extensions.dialogs.showRenameDialog
import ru.tensor.sbis.design.folders.support.extensions.dialogs.showUnshareDialog
import ru.tensor.sbis.design.folders.support.extensions.getFolder
import ru.tensor.sbis.design.folders.support.extensions.getNameById
import ru.tensor.sbis.design.folders.support.listeners.FolderActionListener

/**
 * @author ma.kolpakov
 */
internal class DefaultFolderActionHandler(
    private val appContext: Context,
    private val foldersViewModel: FoldersViewModel,
    private val fragmentManager: FragmentManager,
    private val actionsListener: FolderActionListener,
    private val viewModelKey: String?,
) : FolderActionHandler {

    override fun handleAction(actionType: FolderActionType, folderId: String, folderName: String?) =
        with(foldersViewModel) {
            when (actionType) {
                FolderActionType.CLICK ->
                    actionsListener.opened(foldersViewModel.getFolder(folderId))
                FolderActionType.ADDITIONAL_COMMAND_CLICK ->
                    actionsListener.additionalCommandClicked()
                FolderActionType.ADDITIONAL_COMMAND_TITLE_CLICK -> actionsListener.additionalCommandTitleClicked()
                FolderActionType.ADDITIONAL_COMMAND_ICON_CLICK -> actionsListener.additionalCommandIconClicked()
                FolderActionType.RENAME ->
                    showRenameDialog(
                        appContext,
                        fragmentManager,
                        folderId,
                        folderName,
                        collapsingFolders.value!!.getNameById(folderId),
                        viewModelKey
                    )
                FolderActionType.CREATE ->
                    showCreateDialog(appContext, fragmentManager, folderId, viewModelKey)
                FolderActionType.DELETE ->
                    showDeleteDialog(
                        appContext,
                        fragmentManager,
                        folderId,
                        collapsingFolders.value!!.getNameById(folderId),
                        viewModelKey
                    )
                FolderActionType.UNSHARE ->
                    showUnshareDialog(
                        appContext,
                        fragmentManager,
                        folderId,
                        collapsingFolders.value!!.getNameById(folderId),
                        viewModelKey
                    )
            }
        }
}