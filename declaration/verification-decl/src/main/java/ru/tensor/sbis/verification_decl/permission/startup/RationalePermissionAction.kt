package ru.tensor.sbis.verification_decl.permission.startup

import android.app.Activity
import androidx.annotation.UiThread
import kotlinx.coroutines.flow.Flow

/**
 * Действие, описывающие обоснование запроса разрешений [StartupPermission].
 *
 * @author am.boldinov
 */
fun interface RationalePermissionAction {

    /**
     * Выполняет действие для обоснования запроса разрешения.
     * В случае успеха при подтверждении пользователем результат [Flow] испускает true, иначе false.
     */
    @UiThread
    fun request(activity: Activity): Flow<Boolean>
}

/*class AlertDialogRationaleAction(
    @StringRes
    private val message: Int
) : RationalePermissionAction {

    override fun request(activity: Activity): Flow<Boolean> {
        class DialogContainer {
            private var dialog: AlertDialog? = null

            fun show(dialog: AlertDialog) {
                this.dialog = dialog
                dialog.show()
            }

            fun dismiss() {
                dialog?.dismiss()
                dialog = null
            }
        }
        val flow = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
        val container = DialogContainer()
        val dialog = AlertDialogCreator
            .createMessageDialog(
                activity,
                message = activity.getString(message),
                positiveButtonText = activity.getString(ru.tensor.sbis.common.R.string.common_delete_dialog_positive),
                negativeButtonText = activity.getString(ru.tensor.sbis.common.R.string.common_delete_dialog_negative),
                onPositiveButtonClick = {
                    flow.tryEmit(true)
                    container.dismiss()
                },
                onNegativeButtonClick = {
                    flow.tryEmit(false)
                    container.dismiss()
                }
            ).apply {
                setCancelable(false)
                setCanceledOnTouchOutside(false)
            }
        container.show(dialog)
        return flow
    }
}*/