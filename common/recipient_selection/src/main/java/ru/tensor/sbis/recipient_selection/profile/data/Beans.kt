package ru.tensor.sbis.recipient_selection.profile.data

import androidx.annotation.IntRange
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemId
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.ui.model.recipient.DepartmentSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.PersonSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.persons.PersonName
import ru.tensor.sbis.communicator.generated.RecipientFolder
import ru.tensor.sbis.profiles.generated.EmployeeProfile
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.ContactItem
import ru.tensor.sbis.recipient_selection.profile.mapper.FolderAndGroupItemMapper
import ru.tensor.sbis.recipient_selection.profile.mapper.ProfileAndContactItemMapper

/**
 * Служебные классы для выбора получателей из контактов
 *
 * @author vv.chekurda
 */
internal interface MultiSelectionItemContainer {
    val item: MultiSelectionItem
}

internal sealed class DefaultRecipientSelectorItemModel : RecipientSelectorItemModel {
    override lateinit var meta: SelectorItemMeta
}

internal data class DepartmentSelectorItemModelImpl @JvmOverloads constructor(
    override val id: SelectorItemId,
    override val title: String,
    override val subtitle: String? = null,
    @IntRange(from = 0)
    override val membersCount: Int,
    override val item: MultiSelectionItem
) : DefaultRecipientSelectorItemModel(), DepartmentSelectorItemModel, MultiSelectionItemContainer {

    constructor(folder: RecipientFolder, mapper: FolderAndGroupItemMapper) : this(
        id = folder.uuid.toString(),
        title = folder.name,
        subtitle = folder.chief ?: "",
        membersCount = folder.count,
        item = mapper.apply(folder)
    )

}

internal data class PersonSelectorItemModelImpl @JvmOverloads constructor(
    override val id: SelectorItemId,
    override val title: String,
    override val subtitle: String? = null,
    override val personData: PersonData,
    override val personName: PersonName,
    override val item: MultiSelectionItem
) : DefaultRecipientSelectorItemModel(), PersonSelectorItemModel, MultiSelectionItemContainer {

    constructor(employeeProfile: EmployeeProfile, mapper: ProfileAndContactItemMapper, personData: PersonData) : this(
        id = employeeProfile.person.uuid.toString(),
        title = "${employeeProfile.person.name.last} ${employeeProfile.person.name.first}",
        subtitle = employeeProfile.companyOrDepartment,
        personData = personData,
        personName = PersonName(employeeProfile.person.name.first, employeeProfile.person.name.last, employeeProfile.person.name.patronymic),
        item = mapper.apply(employeeProfile)
    )

    constructor(contact: ContactVM, personData: PersonData) : this(
        id = contact.uuid.toString(),
        title = contact.renderedName,
        subtitle = contact.data1,
        personData = personData,
        personName = contact.name,
        item = ContactItem(contact)
    )
}

private const val AVATAR_PHOTO_SIZE = 124