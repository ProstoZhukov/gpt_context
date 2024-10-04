package ru.tensor.sbis.design.folders.support.extensions.dialogs

import android.content.Context
import androidx.fragment.app.FragmentManager
import io.reactivex.Single
import ru.tensor.sbis.design.folders.support.FoldersProvider
import ru.tensor.sbis.toolbox_decl.Result

internal const val RENAME_FOLDER_DIALOG_CODE = 10

/**
 * Фабрика подписки для диалога переименования папки
 *
 * @author ma.kolpakov
 */
internal class RenameFolderDialogListenerFactory(
    folderId: String,
    private val folderName: String?,
    viewModelKey: String?
) : AbstractFolderDialogListenerFactory(folderId, viewModelKey) {

    override fun createListener(
        context: Context,
        folderId: String,
        foldersProvider: FoldersProvider,
        fragmentManager: FragmentManager?
    ): AbstractFolderDialogListener =
        RenameFolderDialogListener(context, folderId, folderName, foldersProvider)
}

private class RenameFolderDialogListener(
    context: Context,
    folderId: String,
    private val folderName: String?,
    private val foldersProvider: FoldersProvider
) : AbstractFolderDialogListener(context, folderId) {

    override val code: Int = RENAME_FOLDER_DIALOG_CODE

    override fun handleResult(text: String?): Single<Result> = when {
        text.isNullOrBlank() -> Single.error(IllegalArgumentException("Rename dialog returned null text"))
        text == folderName -> Single.fromCallable { Result.SUCCESS }
        else -> foldersProvider.rename(folderId, text.trim())
    }
}