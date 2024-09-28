package ru.tensor.sbis.recipient_selection.profile.contract

import ru.tensor.sbis.person_decl.employee.person_card.factory.PersonCardIntentFactory
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper

/**
 * Внешние зависимости модуля выбора получателей.
 *
 * @author vv.chekurda
 */
interface RecipientSelectionDependency :
    EmployeeProfileControllerWrapper.Provider {

    val personCardIntentFactory: PersonCardIntentFactory?
}