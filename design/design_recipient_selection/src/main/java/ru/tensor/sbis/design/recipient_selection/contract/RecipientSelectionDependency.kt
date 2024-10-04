package ru.tensor.sbis.design.recipient_selection.contract

import ru.tensor.sbis.communication_decl.selection.sources.edo.SelectionFacesSource
import ru.tensor.sbis.design.profile_decl.person.PersonClickListener
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper

/**
 * Внешние зависимости модуля выбора получателей.
 *
 * @author vv.chekurda
 */
interface RecipientSelectionDependency :
    EmployeeProfileControllerWrapper.Provider {

    /**
     * Фабрика intent карточки сотрудника.
     */
    val personClickListener: PersonClickListener?

    /**
     * Источник лиц.
     */
    val selectionFacesSource: SelectionFacesSource?
}