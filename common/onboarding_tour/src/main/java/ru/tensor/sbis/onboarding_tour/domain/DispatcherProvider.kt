package ru.tensor.sbis.onboarding_tour.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Поставщик основных диспетчеров корутин.
 */
internal class DispatcherProvider(
    val io: CoroutineDispatcher,
    val ui: CoroutineDispatcher
) {
    /** Конструктор для реализации по умолчанию. */
    constructor() : this(Dispatchers.IO, Dispatchers.Main)
}