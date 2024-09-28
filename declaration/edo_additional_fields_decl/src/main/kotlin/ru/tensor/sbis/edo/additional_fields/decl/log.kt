package ru.tensor.sbis.edo.additional_fields.decl

import timber.log.Timber

private const val LOG_TAG = "EDO_UI.ADD_FIELDS"

fun Any.logAddFields(message: String) {
    edoLogSimple("${javaClass.simpleName}-${hashCode()}", message)
}

private fun edoLogSimple(context: String, message: String) {
    Timber
        .tag(LOG_TAG)
        .d("$context-${Thread.currentThread().name} | $message")
}