package ru.tensor.sbis.crud4.domain

import ru.tensor.sbis.service.generated.DirectionType

/**
 * Указаывает на начальное направление пагинации в crud4 списке.
 * @author ma.kolpakov
 */
enum class PageDirection {
    /**
     * Прямое направление получения данных.
     */
    FORWARD,

    /**
     * Обратное направление получения данных.
     */
    BACKWARD,

    /**
     * Получение данных в обоих направлениях.
     */
    BOTHWAY,
}

internal fun PageDirection.toDirectionType() = DirectionType.values()[this.ordinal]
