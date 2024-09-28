package ru.tensor.sbis.recipient_selection.employee.ui.holder

import android.view.View
import android.widget.TextView
import ru.tensor.sbis.mvp.multiselection.adapter.MultiSelectionViewHolder
import ru.tensor.sbis.persons.GroupContactVM
import ru.tensor.sbis.recipient_selection.R
import ru.tensor.sbis.recipient_selection.employee.ui.data.item.EmployeesFolderSelectionItem

class EmployeesFolderSelectionViewHolder @JvmOverloads constructor(
    itemView: View,
    isSingleChoice: Boolean = false
) : MultiSelectionViewHolder<EmployeesFolderSelectionItem>(itemView, isSingleChoice) {

    private lateinit var departmentTitle: TextView
    private lateinit var departmentSubtitle: TextView

    override fun initViews() {
        mCheckBox = itemView.findViewById(R.id.checkbox)
        mCheckBoxContainer = itemView.findViewById(R.id.checkbox_container)
        mSeparatorView = itemView.findViewById(R.id.employee_department_separator)
        departmentTitle = itemView.findViewById(R.id.employee_department_title)
        departmentSubtitle = itemView.findViewById(R.id.employee_department_subtitle)
    }

    override fun bind(dataModel: EmployeesFolderSelectionItem) {
        super.bind(dataModel)
        val employeeFolder = dataModel.group
        mCheckBoxContainer.visibility = if (dataModel.canSelectFolder) View.VISIBLE
        else View.GONE
        departmentTitle.text = employeeFolder.groupName
        departmentSubtitle.text = mapDepartmentSubtitle(employeeFolder)
        departmentTitle.requestLayout()
    }

    private fun mapDepartmentSubtitle(employeeFolder: GroupContactVM): String? {
        val displayCount = if (employeeFolder.count > 0) "(${employeeFolder.count})" else ""
        return "${employeeFolder.groupChiefName ?: ""} $displayCount".trim()
    }
}