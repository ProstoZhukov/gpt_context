package ru.tensor.sbis.design.files_picker.view

import android.content.res.Resources
import ru.tensor.sbis.design.files_picker.R
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import timber.log.Timber

val SbisFilesPickerTab.id: String
    get() = javaClass.simpleName

fun Any.logAttachProcess(message: String) {
    Timber.tag("AttachProcessUi").d("${javaClass.simpleName} ${hashCode()} $message")
}

fun pushSelectionLimitNotification(resources: Resources, selectionLimit: Int) {
    SbisPopupNotification.push(
        SbisPopupNotificationStyle.ERROR,
        resources.getQuantityString(
            R.plurals.files_picker_selection_limit_message,
            selectionLimit,
            selectionLimit
        )
    )
}