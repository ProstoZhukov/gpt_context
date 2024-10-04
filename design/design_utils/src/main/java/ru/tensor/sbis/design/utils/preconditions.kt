/**
 * Набор аналогов из Preconditions.kt для публикации ошибок в аналитику на релизных версиях
 *
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.utils

import timber.log.Timber

/**
 * @see check
 */
inline fun checkSafe(value: Boolean, lazyMessage: () -> Any = { "Check failed" }) {
    if (!value) {
        lazyMessage() throwAs ::IllegalStateException
    }
}

/**
 * @see checkNotNull
 */
inline fun <T : Any> checkNotNullSafe(value: T?, lazyMessage: () -> Any = { "Required value was null" }): T? {
    return if (value == null) {
        lazyMessage() throwAs ::IllegalStateException
        null
    } else {
        value
    }
}

/**
 * @see require
 */
inline fun requireSafe(value: Boolean, lazyMessage: () -> Any = { "Failed requirement" }) {
    if (!value) {
        lazyMessage() throwAs ::IllegalArgumentException
    }
}

/**
 * @see requireNotNull
 */
inline fun <T : Any> requireNotNullSafe(value: T?, lazyMessage: () -> Any = { "Required value was null" }): T? {
    return if (value == null) {
        lazyMessage() throwAs ::IllegalArgumentException
        null
    } else {
        value
    }
}

/**
 * Метод позволяет бросить исключение в debug сборке или продолжить цепочку в release со значением по умолчанию.
 * Пример использования:
 * ```
 * val defaultRatio = 0.5F
 * val scaleRatio = when(scale) {
 *     XS -> 0.25F
 *     S -> 0.5F
 *     M -> 1.0F
 *     else -> errorSafe("Scale $scale is not supported for this use case") ?: defaultRatio
 * }
 * ```
 *
 * @see error
 */
fun errorSafe(message: Any): Nothing? =
    if (isDebug()) error(message) else null

/**
 * Бросает исключение, если сборка DEBUG, иначе логирует его.
 */
@PublishedApi
internal inline infix fun Any.throwAs(createException: (String) -> Throwable) {
    toString().run(createException).let { exception -> if (isDebug()) throw exception else Timber.e(exception) }
}

/** @SelfDocumented */
@PublishedApi
internal fun isDebug() = BuildConfig.DEBUG