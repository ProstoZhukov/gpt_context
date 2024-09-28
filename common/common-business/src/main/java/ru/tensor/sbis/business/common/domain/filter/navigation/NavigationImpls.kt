package ru.tensor.sbis.business.common.domain.filter.navigation

/**
 * Постраничная навигация
 * @param limit размерность страницы выборки
 * @param pageNumber номер текущей страницы/разворота
 */
class PageNavigation(
    override var limit: Int = 0,
    override var pageNumber: Int = 0
) : Navigation {
    override val type = NavigationType.PAGE
    override var direction = Direction.FORWARD
    override var offset = 0
}

/**
 * Навигация по курсору
 *
 * @property CURSOR тип курсора
 * @param limit [Navigation.limit]
 * @param offset [Navigation.offset] возможно использование при курсоре для вычитывания из кэша
 * @param direction порядок возвращаемых записей
 * @property position курсор [Direction.FORWARD] - запись позиции (список ключевых полей со значениями), от которой нужно вернуть данные.
 * Для навигации с типом [NavigationType.POSITION]. Для null будет возвращать данные сначала списка.
 * @property positionBackward курсор [Direction.BACKWARD] - запись позиции
 */
class CursorNavigation<CURSOR : Any>(
    override var limit: Int = 0,
    override var offset: Int = 0,
    var position: CURSOR? = null,
    var positionBackward: CURSOR? = null,
    override var direction: Direction = Direction.FORWARD
) : Navigation {
    override val type = NavigationType.POSITION
    override var pageNumber = 0
}

/**
 * Неиспользуемая навигация, например при получении всех записей без навигации
 */
class NoneNavigation : Navigation {
    override val type = NavigationType.NULL
    override var direction = Direction.BOTHWAYS
    override var limit = 0
    override var pageNumber = 0
    override var offset = 0
}


