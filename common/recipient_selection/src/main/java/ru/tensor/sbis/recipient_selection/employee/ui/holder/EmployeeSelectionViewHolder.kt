package ru.tensor.sbis.recipient_selection.employee.ui.holder

import android.view.View
import ru.tensor.sbis.recipient_selection.employee.ui.data.item.EmployeeSelectionItem
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.ContactItem
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.RecipientContactViewHolder

/**
 * Холдер для списка выбора сотрудников
 */
class EmployeeSelectionViewHolder @JvmOverloads constructor(
    itemView: View,
    isSingleChoice: Boolean = false
) : RecipientContactViewHolder(itemView, isSingleChoice) {

    /** @SelfDocumented */
    override fun bind(dataModel: ContactItem) {
        super.bind(dataModel)
        if (!(dataModel as EmployeeSelectionItem).needOpenProfileOnPhotoClick) {
            mPersonPhotoView.setOnClickListener(null)
            mPersonPhotoView.setOnLongClickListener(null)
        }
    }
}