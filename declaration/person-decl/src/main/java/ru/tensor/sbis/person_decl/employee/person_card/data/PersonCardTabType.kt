package ru.tensor.sbis.person_decl.employee.person_card.data

import ru.tensor.sbis.person_decl.motivation.BadgesListFeature.BadgesListFilterOpenArgs

/**
 * Типы вкладок карточки сотрудника
 *
 * @author ra.temnikov
 */
sealed class PersonCardTabType {
    /** Карточка сотрудника */
    object PersonCard : PersonCardTabType()

    /** Зарплата */
    object Salary : PersonCardTabType()

    /** KPI */
    object Kpi : PersonCardTabType()

    /** Райтинги */
    object Ratings : PersonCardTabType()

    /** Достижения */
    class Badge(val args: BadgesListFilterOpenArgs? = null) : PersonCardTabType()

    /** Задачи */
    object Tasks : PersonCardTabType()

    /** Трудовая книга */
    object EmplRecordbook : PersonCardTabType()
}