package ru.tensor.sbis.recipient_selection.employee.ui.holder

import android.view.View
import android.widget.TextView
import ru.tensor.sbis.mvp.multiselection.adapter.MultiSelectionViewHolder
import ru.tensor.sbis.recipient_selection.R
import ru.tensor.sbis.recipient_selection.employee.ui.data.item.EmployeesSelectionPathItem

/**
 * Холдер для [EmployeesSelectionPathItem]
 */
class EmployeesPathSelectionViewHolder @JvmOverloads constructor(
    itemView: View,
    isSingleChoice: Boolean = false
) : MultiSelectionViewHolder<EmployeesSelectionPathItem>(itemView, isSingleChoice) {

    private lateinit var path: TextView

    /** @SelfDocumented */
    override fun initViews() {
        mCheckBox = itemView.findViewById(R.id.checkbox)
        mCheckBoxContainer = itemView.findViewById(R.id.checkbox_container)
        mSeparatorView = itemView.findViewById(R.id.employee_path_separator)
        path = itemView.findViewById(R.id.item_employee_path_view)
    }

    /** @SelfDocumented */
    override fun bind(dataModel: EmployeesSelectionPathItem) {
        super.bind(dataModel)
        path.setText(dataModel.breadCrumbs, TextView.BufferType.SPANNABLE)
    }
}