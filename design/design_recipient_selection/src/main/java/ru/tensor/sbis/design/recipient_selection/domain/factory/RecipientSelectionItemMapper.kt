package ru.tensor.sbis.design.recipient_selection.domain.factory

import ru.tensor.sbis.common_views.SearchSpan
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientDepartmentId
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientPersonId
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientId as RecipientSelectionId
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design_selection.contract.data.SelectionItemMapper
import ru.tensor.sbis.persons.PersonName
import ru.tensor.sbis.recipients.generated.EmployeeProfileId
import ru.tensor.sbis.recipients.generated.ProfileFolderId
import ru.tensor.sbis.recipients.generated.RecipientId
import ru.tensor.sbis.recipients.generated.RecipientViewModel
import ru.tensor.sbis.recipients.generated.RecipientViewModelData
import javax.inject.Inject

/**
 * Маппер компонента выбора получателей.
 * Производит маппинг моделей контроллера в соответствующие модели списка UI.
 *
 * @property config настрока компонента выбора получателей.
 *
 * @author vv.chekurda
 */
internal class RecipientSelectionItemMapper @Inject constructor(
    private val config: RecipientSelectionConfig
) : SelectionItemMapper<RecipientViewModel, RecipientId, RecipientItem, RecipientSelectionId> {

    override fun map(item: RecipientViewModel): RecipientItem =
        when (item.data.storedType) {
            RecipientViewModelData.Companion.employeeProfile -> mapPersonItem(item)
            RecipientViewModelData.Companion.recipientFolder -> mapFolderItem(item)
            else -> throw IllegalArgumentException("Unsupported selection item type = ${item.data.storedType}")
        }

    override fun getId(id: RecipientSelectionId): RecipientId =
        RecipientId().apply {
            when (id) {
                is RecipientPersonId -> fieldEmployeeProfileId = EmployeeProfileId(id.uuid)
                is RecipientDepartmentId -> fieldProfileFolderId = ProfileFolderId(id.uuid)
            }
        }

    private fun mapPersonItem(
        model: RecipientViewModel
    ): RecipientItem = with(requireNotNull(model.data.fieldEmployeeProfile)) {
        RecipientPersonItem(
            id = RecipientPersonId(model.id),
            faceId = person.localFace,
            photoData = PersonData(
                uuid = person.uuid,
                photoUrl = person.photoUrl,
                initialsStubData = person.photoDecoration?.let { decor ->
                    InitialsStubData(
                        decor.initials,
                        decor.backgroundColorHex
                    )
                }
            ),
            title = model.displayTitle,
            subtitle = companyOrDepartment,
            personName = with(person.name) { PersonName(first, last, patronymic) },
            isInMyCompany = inMyCompany,
            titleHighlights = model.nameHighlight.map { SearchSpan(it.start, it.end) },
            position = position
        )
    }

    private fun mapFolderItem(
        model: RecipientViewModel
    ): RecipientItem = with(requireNotNull(model.data.fieldRecipientFolder)) {
        RecipientDepartmentItem(
            id = RecipientDepartmentId(model.id),
            faceId = faceId,
            title = model.displayTitle,
            subtitle = FOLDER_SUBTITLE_FORMAT.format(chief, employeeCount).trim(),
            counter = employeeCount,
            selectable = config.isDepartmentsSelectable,
            titleHighlights = model.nameHighlight.map { SearchSpan(it.start, it.end) }
        )
    }
}

private const val FOLDER_SUBTITLE_FORMAT = "%s (%d)"