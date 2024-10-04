package ru.tensor.sbis.design.folders.support.extensions.dialogs

import android.content.Context
import androidx.fragment.app.FragmentManager
import io.reactivex.Single
import ru.tensor.sbis.toolbox_decl.Result
import ru.tensor.sbis.design.folders.support.FoldersProvider

internal const val UNSHARE_FOLDER_DIALOG_CODE = 13

/**
 * Фабрика подписки для диалога отмены расшаривания папки
 *
 * @author ma.kolpakov
 */
internal class UnshareFolderDialogListenerFactory(
    folderId: String,
    viewModelKey: String?
) : AbstractFolderDialogListenerFactory(folderId, viewModelKey) {

    override fun createListener(
        context: Context,
        folderId: String,
        foldersProvider: FoldersProvider,
        fragmentManager: FragmentManager?
    ): AbstractFolderDialogListener =
        UnshareFolderDialogListener(context, folderId, foldersProvider)
}

private class UnshareFolderDialogListener(
    context: Context,
    folderId: String,
    private val foldersProvider: FoldersProvider
) : AbstractFolderDialogListener(context, folderId) {

    override val code: Int = UNSHARE_FOLDER_DIALOG_CODE

    override fun handleResult(text: String?): Single<Result> =
        foldersProvider.unshare(folderId)
}