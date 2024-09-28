/**
 * Набор вспомогательных функций для работы с диалогами папок
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.folders.support.extensions.dialogs

import android.content.Context
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.design.folders.R
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation

internal fun showRenameDialog(
    context: Context,
    fragmentManager: FragmentManager,
    folderId: String,
    folderName: String?,
    initialText: String?,
    viewModelKey: String?,
) = PopupConfirmation.newEditTextInstance(
    requestCode = RENAME_FOLDER_DIALOG_CODE,
    initialText = initialText,
    message = context.getString(R.string.design_folders_enter_folder_name_message),
    hint = null,
    canNotBeBlank = true
).setListenerFactory(RenameFolderDialogListenerFactory(folderId, folderName, viewModelKey))
    .setEventProcessingRequired(true)
    .requestNegativeButton(context.getString(R.string.design_folders_dialog_decline))
    .requestPositiveButton(context.getString(R.string.design_folders_edit_dialog_confirm))
    .show(fragmentManager, null)

internal fun showCreateDialog(
    context: Context,
    fragmentManager: FragmentManager,
    folderId: String,
    viewModelKey: String?,
    onlyRootFolder: Boolean = false
) = PopupConfirmation.newEditTextInstance(
    requestCode = CREATE_FOLDER_DIALOG_CODE,
    initialText = null,
    message = if (onlyRootFolder)
        context.getString(R.string.design_folders_enter_new_folder_name_message_only_root_folder)
    else context.getString(R.string.design_folders_enter_new_folder_name_message),
    hint = context.getString(R.string.design_folders_enter_folder_name_message),
    canNotBeBlank = true
).setListenerFactory(CreateFolderDialogListenerFactory(folderId, viewModelKey))
    .setEventProcessingRequired(true)
    .requestNegativeButton(context.getString(R.string.design_folders_dialog_decline))
    .requestPositiveButton(context.getString(R.string.design_folders_create_dialog_confirm))
    .show(fragmentManager, null)

internal fun showDeleteDialog(
    context: Context,
    fragmentManager: FragmentManager,
    folderId: String,
    folderTitle: String,
    viewModelKey: String?,
) = PopupConfirmation.newMessageInstance(
    DELETE_FOLDER_DIALOG_CODE,
    context.getString(R.string.design_folders_delete_message)
)
    .setListenerFactory(DeleteFolderDialogListenerFactory(folderId, viewModelKey))
    .requestTitle(context.getString(R.string.design_folders_delete_title_template, folderTitle))
    .setEventProcessingRequired(true)
    .requestNegativeButton(context.getString(R.string.design_folders_dialog_decline))
    .requestPositiveButton(context.getString(R.string.design_folders_delete_dialog_confirm), true)
    .show(fragmentManager, null)

internal fun showUnshareDialog(
    context: Context,
    fragmentManager: FragmentManager,
    folderId: String,
    folderTitle: String,
    viewModelKey: String?,
) = PopupConfirmation.newSimpleInstance(UNSHARE_FOLDER_DIALOG_CODE)
    .setListenerFactory(UnshareFolderDialogListenerFactory(folderId, viewModelKey))
    .requestTitle(context.getString(R.string.design_folders_cancel_folder_sharing_message, folderTitle))
    .setEventProcessingRequired(true)
    .requestNegativeButton(context.getString(R.string.design_folders_no_action))
    .requestPositiveButton(context.getString(R.string.design_folders_yes_action))
    .show(fragmentManager, null)
