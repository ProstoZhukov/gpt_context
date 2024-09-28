package ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts

import androidx.annotation.StringRes
import ru.tensor.sbis.design.confirmation_dialog.ButtonModel
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationButtonId
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationDialogStyle
import ru.tensor.sbis.mvp.presenter.BaseErrorView

/** Интерфейс для отображения всплывающих уведомлений на экране переписки. */
interface PopupErrorView : BaseErrorView {

    /** @SelfDocumented */
    fun showSnackbarError(@StringRes errorTextId: Int)

    /** @SelfDocumented */
    fun showToast(@StringRes toastTextId: Int)

    /** @SelfDocumented */
    fun showToast(message: String)

    /**
     * Показать панель-информер с положительным результатом.
     */
    fun showSuccessPopup(@StringRes textId: Int) = Unit

    /** @SelfDocumented */
    fun showConfirmationDialog(
        text: CharSequence?,
        buttons: List<ButtonModel<ConfirmationButtonId>>,
        tag: String,
        style: ConfirmationDialogStyle = ConfirmationDialogStyle.ERROR
    ) = Unit

    /** @SelfDocumented */
    fun showOkCancelDialog(
        message: String? = null,
        comment: String? = null,
        tag: String,
        style: ConfirmationDialogStyle = ConfirmationDialogStyle.PRIMARY
    ) = Unit
}

interface PopupErrorHandler {

    fun onConfirmationDialogButtonClicked(tag: String?, id: String) = Unit
}