package ru.tensor.sbis.recipient_selection.employeenew.data

import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemId
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.ui.model.recipient.DepartmentSelectorItemModel

/**
 * Вью-модель папки с сотрудниками.
 *
 * @author sr.golovkin on 31.07.2020
 */

data class EmployeeFolderSelectorItemModel(
    override val title: String,
    override val subtitle: String?,
    override val id: SelectorItemId,
    override val hasNestedItems: Boolean,
    override val membersCount: Int
) : DepartmentSelectorItemModel,
    BaseEmployeeSelectorItemModel {

    constructor(
    title: String,
    subtitle: String?,
    id: SelectorItemId,
    hasNestedItems: Boolean,
    membersCount: Int,
    oldVm: MultiSelectionItem
    ): this(title, subtitle, id, hasNestedItems, membersCount) {
        this.oldVm = oldVm
    }

    override lateinit var oldVm: MultiSelectionItem
    override lateinit var meta: SelectorItemMeta
}