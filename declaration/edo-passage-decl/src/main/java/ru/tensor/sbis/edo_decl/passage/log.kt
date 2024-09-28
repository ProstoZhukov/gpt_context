package ru.tensor.sbis.edo_decl.passage

import timber.log.Timber

const val SINGLE_PASSAGE_TAG = "UiSelectAndExecute"
const val MASS_PASSAGES_TAG = "MassPassagesTag"

/**
 * Вывести лог по одиночным переходам в случае debug сборки.
 */
fun logSinglePassage(message: String, withThread: Boolean = true) {
    log(tag = SINGLE_PASSAGE_TAG, message = message, withThread = withThread)
}

/**
 * Вывести лог по массовым переходам в случае debug сборки.
 */
fun logMassPassages(message: String, withThread: Boolean = true) {
    log(tag = MASS_PASSAGES_TAG, message = message, withThread = withThread)
}

/**
 * Вывести лог в случае debug сборки
 */
fun log(tag: String, message: String, withThread: Boolean = true) {
    Timber.tag(tag).d("${if (withThread) "${Thread.currentThread().name} " else ""}$message")
}