package ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder.widget.dialogs

import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.design.buttons.base.models.style.PrimaryButtonStyle
import ru.tensor.sbis.design.confirmation_dialog.BaseContentProvider
import ru.tensor.sbis.design.confirmation_dialog.ButtonModel
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationButtonId
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationDialog
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationDialogStyle

/**
 * Контракт для взаимодействия с "успешным" диалогом.
 *
 * @param lifecycleOwner
 * @param fragmentManager
 * @param onFinish действие по закрытию диалога.
 *
 * @author kv.martyshenko
 */
internal class CompleteDialogContract(
    lifecycleOwner: LifecycleOwner,
    private val fragmentManager: FragmentManager,
    onFinish: () -> Unit
) {

    init {
        fragmentManager.setFragmentResultListener(
            DEFAULT_REQUEST_KEY,
            lifecycleOwner
        ) { key, _ ->
            if (key != DEFAULT_REQUEST_KEY) return@setFragmentResultListener

            onFinish()
        }
    }

    fun show(title: String?, description: String) {
        ConfirmationDialog(
            contentProvider = BaseContentProvider(
                message = title,
                comment = description
            ),
            buttons = {
                listOf(
                    ButtonModel(
                        ConfirmationButtonId.OK,
                        ru.tensor.sbis.design.design_confirmation.R.string.design_confirmation_dialog_button_ok,
                        PrimaryButtonStyle
                    )
                )
            },
            style = ConfirmationDialogStyle.SUCCESS,
            tag = DEFAULT_REQUEST_KEY,
            isCancellable = false,
            buttonCallback = { viewModel, _ ->
                viewModel.requireParentFragment().childFragmentManager.setFragmentResult(
                    DEFAULT_REQUEST_KEY,
                    bundleOf()
                )
                viewModel.dismiss()
            }
        ).show(fragmentManager)
    }

    private companion object {
        private const val DEFAULT_REQUEST_KEY = "complete_request_key"
    }
}