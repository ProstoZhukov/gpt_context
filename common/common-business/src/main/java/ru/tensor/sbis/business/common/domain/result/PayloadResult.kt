package ru.tensor.sbis.business.common.domain.result

/**
 * Интерфейс ответа с полезной нагрузкой
 *
 * @property error смежная ошибка возникшая при получении данных, но не прервавшая обработку
 * @property fromRefreshedCache результат получен из кэша ПОСЛЕ первичной синхронизации,
 * т.е. по callback о завершении синхронизации!!!
 *
 * @property isEmpty true если НЕТ данных
 * @property isNotEmpty true если ЕСТЬ данные
 * @property isEmptyAndHasError true если НЕТ данных и иментся ошибка [error]
 * @property isEmptyAfterRefresh true если НЕТ списочных данных после синхронизации [fromRefreshedCache]
 * @property isNotEmptyAfterRefresh true если ЕСТЬ списочные данные после синхронизации [fromRefreshedCache]
 */
interface PayloadResult {

    var error: Throwable?

    var fromRefreshedCache: Boolean

    val isEmpty: Boolean

    val isNotEmpty: Boolean
        get() = isEmpty.not()

    val isEmptyAndHasError: Boolean
        get() = isEmpty && error != null

    val isEmptyAfterRefresh: Boolean
        get() = isEmpty && fromRefreshedCache

    val isNotEmptyAfterRefresh: Boolean
        get() = isNotEmpty && fromRefreshedCache
}