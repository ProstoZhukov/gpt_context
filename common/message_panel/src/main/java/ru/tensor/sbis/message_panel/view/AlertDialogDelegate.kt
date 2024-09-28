package ru.tensor.sbis.message_panel.view

import androidx.fragment.app.Fragment
import io.reactivex.Observable
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationDialog
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationDialogStyle

/**
 * Данные для показа алерт диалога
 */
data class AlertDialogData(
    val message: String
)

/**
 * Делегат для подписки на события показа алерт диалога.
 *
 * @author vv.chekurda
 */
class AlertDialogDelegate(private val fragment: Fragment) {

    @CheckReturnValue
    fun bind(liveData: Observable<AlertDialogData>): Disposable =
        liveData.subscribe { showAlertDialog(it) }

    private fun showAlertDialog(alertData: AlertDialogData) {
        ConfirmationDialog.OkDialog(
            message = alertData.message,
            comment = null,
            style = ConfirmationDialogStyle.ERROR,
        ) { container, _ ->
            container.closeContainer()
        }.show(fragment.childFragmentManager)
    }
}