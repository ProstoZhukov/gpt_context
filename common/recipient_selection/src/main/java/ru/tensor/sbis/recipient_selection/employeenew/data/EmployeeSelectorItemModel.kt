package ru.tensor.sbis.recipient_selection.employeenew.data

import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemId
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.ui.model.recipient.PersonSelectorItemModel
import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem
import ru.tensor.sbis.persons.PersonName

/**
 * Вью-модель сотрудника для экрана выбора сотрудника
 *
 * @author sr.golovkin on 03.08.2020
 */
data class EmployeeSelectorItemModel(
    override val personData: PersonData,
    override val personName: PersonName,
    override val title: String,
    override val subtitle: String?,
    override val id: SelectorItemId,
    override val hasNestedItems: Boolean = false
) : PersonSelectorItemModel,
    BaseEmployeeSelectorItemModel {

    constructor(
        personData: PersonData,
        personName: PersonName,
        title: String,
        subtitle: String?,
        id: SelectorItemId,
        oldVm: MultiSelectionItem,
        hasNestedItems: Boolean = false
    ): this(personData, personName, title, subtitle, id, hasNestedItems) {
        this.oldVm = oldVm
    }

    override lateinit var oldVm: MultiSelectionItem
    override lateinit var meta: SelectorItemMeta
}