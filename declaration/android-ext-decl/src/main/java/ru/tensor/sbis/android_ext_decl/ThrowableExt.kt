package ru.tensor.sbis.android_ext_decl

import timber.log.Timber

/**
 * Безопасно обрабатывает нефатальное, но очень нежелательное исключение.
 * В дебажном билде Throwable будет кинуто, в релизном - выведено в лог.
 */
fun Throwable.throwIfDebug() {
    if (BuildConfig.DEBUG) {
        throw this
    } else {
        Timber.e(this)
    }
}