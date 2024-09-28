package ru.tensor.sbis.person_decl.employee.person_card

import ru.tensor.sbis.person_decl.employee.person_card.factory.PersonCardFragmentFactory
import ru.tensor.sbis.person_decl.employee.person_card.factory.PersonCardIntentFactory

/**
 * Поставщик карточки сотрудника
 * @see PersonCardFragmentFactory
 * @see PersonCardIntentFactory
 *
 * @author ra.temnikov
 */
interface PersonCardProvider :
    PersonCardFragmentFactory,
    PersonCardIntentFactory