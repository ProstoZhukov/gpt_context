package ru.tensor.sbis.common_attachments

import timber.log.Timber

fun Any.logAttachProcess(message: String) {
    Timber.tag("AttachProcessUi").d("${javaClass.simpleName} ${hashCode()} $message")
}