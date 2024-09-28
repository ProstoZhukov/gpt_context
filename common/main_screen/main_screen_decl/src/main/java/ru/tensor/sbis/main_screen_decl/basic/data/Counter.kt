package ru.tensor.sbis.main_screen_decl.basic.data

import kotlinx.coroutines.flow.Flow

/**
 * Счётчик для иконок в шапке.
 *
 * @author us.bessonov
 */
sealed interface Counter

/**
 * Счётчик, получаемый от микросервиса.
 *
 * @param name имя счётчика для идентификации в наборе счётчиков с сервиса.
 * @param counterSource какое из доступных значений счётчика использовать.
 */
class ServiceCounter(
    val name: String,
    val counterSource: CounterSource = CounterSource.UNREAD
): Counter

/**
 * Пользовательский счётчик.
 *
 * @param counterFlow [Flow] с актуальным значением счётчика.
 */
class CustomCounter(val counterFlow: Flow<Int>): Counter

/**
 * Тип значения счётчика.
 *
 * @see `BnpCounter`
 */
enum class CounterSource {
    /** Число непрочитанных. */
    UNREAD,
    /** Число непросмотренных. */
    UNSEEN,
    /** Общее число. */
    TOTAL
}