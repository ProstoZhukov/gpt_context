package ru.tensor.sbis.recipient_selection.employee.ui.data.item

import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.ContactItem
import java.util.*

/**
 * Класс - модель, которая отображает элемент списка из компонента выбора сотрудников
 */
class EmployeeSelectionItem(
        contact: ContactVM,
        val faceId: Long,
        val photoId: String,
        val needOpenProfileOnPhotoClick: Boolean,
        val employeeUuid: UUID? = null
        ) : ContactItem(contact) {

    /** @SelfDocumented */
    override fun getViewHolderClass() = ru.tensor.sbis.recipient_selection.employee.ui.holder.EmployeeSelectionViewHolder::class.java
}