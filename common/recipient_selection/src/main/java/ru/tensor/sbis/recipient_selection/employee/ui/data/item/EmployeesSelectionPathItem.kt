package ru.tensor.sbis.recipient_selection.employee.ui.data.item

import android.text.SpannableString
import ru.tensor.sbis.persons.GroupContactVM
import ru.tensor.sbis.recipient_selection.R
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.GroupItem

/**
 * Класс - элемент списка, который отображает путь выбранного сотрудника
 */
class EmployeesSelectionPathItem (
        val breadCrumbs: SpannableString,
        lastGroupVM: GroupContactVM
) : GroupItem(lastGroupVM) {

    companion object {
        const val EMPLOYEE_PATH_TYPE = "multi_selection_item.employee_path"
    }

    /** @SelfDocumented */
    override fun getViewHolderClass() = ru.tensor.sbis.recipient_selection.employee.ui.holder.EmployeesPathSelectionViewHolder::class.java

    /** @SelfDocumented */
    override fun getHolderLayoutResId() = R.layout.recipient_selection_item_employee_selection_path

    /** @SelfDocumented */
    override fun getItemType() = EMPLOYEE_PATH_TYPE
}