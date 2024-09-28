package ru.tensor.sbis.common.util

import ru.tensor.sbis.common.BuildConfig
import timber.log.Timber

/** @SelfDocumented */
inline fun illegalArg(lazyMessage: () -> String = { "Required value was null" }) {
    safeThrow(IllegalArgumentException(lazyMessage()))
}

/** @SelfDocumented */
inline fun illegalState(lazyMessage: () -> String = { "Required value was null" }) {
    safeThrow(IllegalStateException(lazyMessage()))
}

/** @SelfDocumented */
fun safeThrow(throwable: Throwable) {
    safeThrow("", throwable)
}

/** @SelfDocumented */
fun safeThrow(timberMessage: String, throwable: Throwable) {
    safeThrow(timberMessage) { throw throwable }
}

/** @SelfDocumented */
inline fun safeThrow(throwException: () -> Nothing) {
    safeThrow("", throwException)
}

/**
 * Безопасно выполнить [action] для [value] если тот не null, иначе вывод ошибки
 */
inline fun <T : Any> safeRunOnNullable(value: T?, action: T.() -> Unit) =
    value?.action() ?: safeThrow(NullPointerException("Required value was null."))


/**
 * Безопасно выполнить [action] для [value] если тот не null и вернуть результат, иначе вывод ошибки
 *
 * @return результат выполнения действия или null
 */
inline fun <reified T, R> safeCallOnNullable(value: T?, noinline action: ((T) -> R)?): R? {
    return if (value == null) {
        val className = T::class.java.name
        val message = "The \"$className\" is null, action unavailable!"
        safeThrow(message, UnsupportedOperationException(message))
        null
    } else {
        action?.invoke(value)
    }
}

/**
 * Безопасный throw. В дебаге упадёт, в релизе - только выведет ошибку в лог
 */
inline fun safeThrow(timberMessage: String, throwException: () -> Nothing) {
    try {
        throwException()
    } catch (throwable: Throwable) {
        if (BuildConfig.DEBUG) {
            throw throwable
        } else {
            Timber.e(throwable, timberMessage)
        }
    }
}
