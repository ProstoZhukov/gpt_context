package ru.tensor.sbis.design.gallery.impl.utils

import android.content.res.Resources
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import timber.log.Timber
import ru.tensor.sbis.design.gallery.R

internal fun pushSizeLimitNotification(resources: Resources, sizeLimit: Int) {
    SbisPopupNotification.push(
        SbisPopupNotificationStyle.ERROR,
        String.format(resources.getString(R.string.design_gallery_size_limit_message), sizeLimit)
    )
}

fun Any.logAttachProcess(message: String) {
    Timber.tag("AttachProcessUi").d("${javaClass.simpleName} ${hashCode()} $message")
}