package ru.tensor.sbis.hallscheme.v2.util

/**
 * @SelfDocumented
 */
internal inline fun <T> unsafeLazy(crossinline initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE) { initializer() }
