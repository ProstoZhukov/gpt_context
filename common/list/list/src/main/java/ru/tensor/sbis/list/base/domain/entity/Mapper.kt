package ru.tensor.sbis.list.base.domain.entity

import ru.tensor.sbis.list.view.utils.ListData

/**
 * Трансформация данных микросервиса в удобный для представления вид.
 * @param FROM тип данных микросервиса, возвращаемых методами list и refresh.
 */
interface Mapper<FROM> {
    /**
     * @param from List<FROM> данные микросервиса, возвращаемых методами list и refresh.
     * @return ListData
     */
    fun map(from: List<FROM>): ListData
}