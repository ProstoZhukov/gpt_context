package ru.tensor.sbis.design.folders.support.extensions.dialogs

import android.content.Context
import androidx.fragment.app.FragmentManager
import io.reactivex.Single
import ru.tensor.sbis.toolbox_decl.Result
import ru.tensor.sbis.design.folders.support.FoldersProvider

internal const val CREATE_FOLDER_DIALOG_CODE = 11

/**
 * Фабрика подписки для диалога создания папки
 *
 * @author ma.kolpakov
 */
internal class CreateFolderDialogListenerFactory(
    folderId: String,
    viewModelKey: String?
) : AbstractFolderDialogListenerFactory(folderId, viewModelKey) {

    override fun createListener(
        context: Context,
        folderId: String,
        foldersProvider: FoldersProvider,
        fragmentManager: FragmentManager?
    ): AbstractFolderDialogListener =
        CreateFolderDialogListener(context, folderId, foldersProvider, fragmentManager)
}

private class CreateFolderDialogListener(
    context: Context,
    folderId: String,
    private val foldersProvider: FoldersProvider,
    fragmentManager: FragmentManager?
) : AbstractFolderDialogListener(context, folderId, fragmentManager) {

    override val code: Int = CREATE_FOLDER_DIALOG_CODE

    override fun handleResult(text: String?): Single<Result> =
        if (text.isNullOrBlank())
            Single.error(IllegalArgumentException("Create dialog returned null text"))
        else
            foldersProvider.create(folderId, text.trim())
}