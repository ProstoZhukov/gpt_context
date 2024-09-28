package ru.tensor.sbis.recipient_selection.employee.ui.data.item

import ru.tensor.sbis.persons.GroupContactVM
import ru.tensor.sbis.recipient_selection.R
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.GroupItem

/**
 * Класс - элемент списка папок сотрудников (Например: Разработка)
 */
class EmployeesFolderSelectionItem(
    groupVM: GroupContactVM,
    val faceId: Long,
    val canSelectFolder: Boolean
) : GroupItem(groupVM) {

    /** @SelfDocumented */
    override fun getViewHolderClass() = ru.tensor.sbis.recipient_selection.employee.ui.holder.EmployeesFolderSelectionViewHolder::class.java

    /** @SelfDocumented */
    override fun getHolderLayoutResId() = R.layout.recipient_selection_item_employee_selection_folder
}