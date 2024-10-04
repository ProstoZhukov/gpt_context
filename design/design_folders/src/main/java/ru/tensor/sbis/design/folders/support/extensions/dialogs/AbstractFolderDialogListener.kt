package ru.tensor.sbis.design.folders.support.extensions.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Single
import ru.tensor.sbis.toolbox_decl.Result
import ru.tensor.sbis.design.folders.support.FoldersProvider
import ru.tensor.sbis.design.folders.support.FoldersViewModel
import ru.tensor.sbis.design.utils.requireSafe
import ru.tensor.sbis.modalwindows.dialogalert.BaseAlertDialogFragment
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation
import timber.log.Timber
import ru.tensor.sbis.base_components.R as RBaseComponents

/**
 * Базовая реализация [PopupConfirmation.DialogYesNoWithTextListener], которая описывает базовый фкнционал подписок
 * и проверяет их использование по назначению
 *
 * @author ma.kolpakov
 */
internal abstract class AbstractFolderDialogListener(
    private val context: Context,
    protected val folderId: String,
    private val fragmentManager: FragmentManager? = null
) : PopupConfirmation.DialogYesNoWithTextListener {

    protected abstract val code: Int

    @SuppressLint("CheckResult" /* Все действия не зависят от отображения на экране */)
    final override fun onYes(requestCode: Int, text: String?) {
        requireSafe(code == requestCode) { "Request code expected $code, but received $requestCode" }
        handleResult(text).subscribe(
            { result ->
                if (!result.success) {
                    fragmentManager?.let {
                        val positiveButtonText =
                            context.getString(RBaseComponents.string.base_components_dialog_button_ok)
                        PopupConfirmation.newMessageInstance(code, result.errorText)
                            .requestPositiveButton(positiveButtonText)
                            .show(it, PopupConfirmation::class.simpleName)
                    } ?: Toast.makeText(context, result.errorText, Toast.LENGTH_LONG).show()
                }
            },
            Timber::e
        )
    }

    protected abstract fun handleResult(text: String?): Single<Result>
}

/**
 * Базовая реализация сериализуемой фабрики для [AbstractFolderDialogListener]
 */
internal abstract class AbstractFolderDialogListenerFactory(
    private val folderId: String,
    private val viewModelKey: String?
) : BaseAlertDialogFragment.ListenerFactory {

    final override fun invoke(dialog: BaseAlertDialogFragment): AbstractFolderDialogListener {
        return createListener(
            dialog.requireContext(),
            folderId,
            resolveFoldersProvider(dialog),
            // дополнительные диалоги должны сохраниться при закррытии основного
            dialog.parentFragmentManager
        )
    }

    protected abstract fun createListener(
        context: Context,
        folderId: String,
        foldersProvider: FoldersProvider,
        fragmentManager: FragmentManager? = null
    ): AbstractFolderDialogListener

    private fun resolveFoldersProvider(dialog: BaseAlertDialogFragment): FoldersProvider {
        val vmProvider = when (val parent = dialog.parentFragment) {
            is Fragment -> ViewModelProvider(parent)
            else -> ViewModelProvider(dialog.requireActivity())
        }
        return if (viewModelKey == null)
            vmProvider[FoldersViewModel::class.java].foldersProvider
        else
            vmProvider.get(viewModelKey, FoldersViewModel::class.java).foldersProvider
    }
}