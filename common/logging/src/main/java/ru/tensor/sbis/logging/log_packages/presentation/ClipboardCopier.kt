package ru.tensor.sbis.logging.log_packages.presentation

import android.view.View
import ru.tensor.sbis.common.util.ClipboardManager
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.logging.R
import javax.inject.Inject

/**
 * Выполняет копирование данных в буфер обмена с показом нотификации.
 */
class ClipboardCopier @Inject constructor(
    private val clipboardManager: ClipboardManager
) {
    /**@SelfDocumented*/
    fun copy(data: LogPackageItemViewModel, view: View) {
        data.copyToClipboard(clipboardManager)
        SbisPopupNotification.push(
            view.context,
            SbisPopupNotificationStyle.SUCCESS,
            view.context.getString(R.string.logging_copy_to_clipboard)
        )
    }
}