package ru.tensor.sbis.business.common.domain.filter.navigation

/**
 * Навигация. Интерфейс предназначенный для создания структуры данных, которая конвертируется в JNI модель
 * навигации фасада микросервиса и передаётся в качестве значения аргумента "навигация" при вызове списочных методов
 *
 * Предназначен для следующих подходов к навигации:
 * - по смещению [NavigationType.OFFSET]
 * - постраничная [NavigationType.PAGE]
 * - по курсору [NavigationType.POSITION]
 *
 * @property type тип навигации
 * @property direction направление навигации, только для [NavigationType.POSITION]
 * @property limit количество запрашиваемых записей, общее понятие для всех типов навигации:
 * [NavigationType.POSITION], [NavigationType.PAGE], [NavigationType.OFFSET]
 * @property pageNumber номер текущей страницы/разворота, только для [NavigationType.PAGE]
 * @property offset смещение, неотрицательное значение означает смещение сначала, отрицательное - с конца;
 * только для [NavigationType.OFFSET] и опционально [NavigationType.PAGE]
 */
interface Navigation {
    val type: NavigationType
    var direction: Direction
    var limit: Int
    var pageNumber: Int
    var offset: Int
}