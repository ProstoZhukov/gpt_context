package ru.tensor.sbis.sale_point_qr_binder.internal.screens.binder.widget.dialogs

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import ru.tensor.sbis.android_ext_decl.args.KeySpec
import ru.tensor.sbis.android_ext_decl.args.boolean
import ru.tensor.sbis.android_ext_decl.args.getKeySpec
import ru.tensor.sbis.android_ext_decl.args.nonNull
import ru.tensor.sbis.android_ext_decl.args.putKeySpec
import ru.tensor.sbis.design.buttons.base.models.style.PrimaryButtonStyle
import ru.tensor.sbis.design.confirmation_dialog.BaseContentProvider
import ru.tensor.sbis.design.confirmation_dialog.ButtonModel
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationButtonId
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationDialog
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationDialogStyle
import ru.tensor.sbis.sale_point_qr_binder.R

/**
 * Контракт для взаимодействия с диалогом об ошибке.
 *
 * @param lifecycleOwner
 * @param fragmentManager
 * @param onRepeat действие по кнопке "повторить".
 * @param onCancel действие по кнопке "отменить".
 *
 * @author kv.martyshenko
 */
internal class ErrorDialogContact(
    lifecycleOwner: LifecycleOwner,
    private val fragmentManager: FragmentManager,
    onRepeat: () -> Unit,
    onCancel: () -> Unit
) {

    init {
        fragmentManager.setFragmentResultListener(
            DEFAULT_REQUEST_KEY,
            lifecycleOwner
        ) { key, result ->
            if (key != DEFAULT_REQUEST_KEY) return@setFragmentResultListener

            val needRepeat = result.getKeySpec(repeatKeySpec)
            if (needRepeat) {
                onRepeat()
            } else {
                onCancel()
            }
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
                        ConfirmationButtonId.YES,
                        R.string.spqrb_action_scan_again,
                        PrimaryButtonStyle
                    ),
                    ButtonModel(
                        ConfirmationButtonId.NO,
                        R.string.spqrb_action_cancel
                    )
                )
            },
            style = ConfirmationDialogStyle.ERROR,
            isCancellable = true,
            buttonCallback = { viewModel, buttonId ->
                viewModel.requireParentFragment().childFragmentManager.setFragmentResult(
                    DEFAULT_REQUEST_KEY,
                    Bundle().apply {
                        putKeySpec(repeatKeySpec, buttonId == ConfirmationButtonId.YES)
                    }
                )
                viewModel.dismiss()
            }
        ).show(fragmentManager)
    }

    private companion object {
        private const val DEFAULT_REQUEST_KEY = "error_request_key"

        private val repeatKeySpec = KeySpec.boolean("repeat").nonNull()
    }
}