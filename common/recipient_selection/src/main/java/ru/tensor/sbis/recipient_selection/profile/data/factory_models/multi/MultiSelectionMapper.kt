package ru.tensor.sbis.recipient_selection.profile.data.factory_models.multi

import ru.tensor.sbis.common.util.map
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.recipient_selection.profile.data.DepartmentSelectorItemModelImpl
import ru.tensor.sbis.recipient_selection.profile.data.PersonSelectorItemModelImpl
import ru.tensor.sbis.recipient_selection.profile.data.factory_models.ProfilesFoldersResult
import ru.tensor.sbis.recipient_selection.profile.mapper.FolderAndGroupItemMapper
import ru.tensor.sbis.recipient_selection.profile.mapper.ProfileAndContactItemMapper
import ru.tensor.sbis.recipient_selection.profile.ui.mapPersonDecorationToInitialsStubData

/**
 * Реализация сериализуемой фабрики зависимостей для компонента выбора получателей
 *
 * @author vv.chekurda
 */
internal class MultiSelectionMapper(
    private val profileAndContactMapper: ProfileAndContactItemMapper,
    private val folderAndGroupMapper: FolderAndGroupItemMapper,
) : ListMapper<ProfilesFoldersResult, RecipientSelectorItemModel> {

    override fun invoke(result: ProfilesFoldersResult): List<RecipientSelectorItemModel> =
        result.profiles.map { profile ->
            val personData = PersonData(
                profile.person.uuid,
                profile.person.photoUrl,
                profile.person.photoDecoration.mapPersonDecorationToInitialsStubData()
            )
            PersonSelectorItemModelImpl(profile, profileAndContactMapper, personData)
        }
            .plus(result.folders.map { DepartmentSelectorItemModelImpl(it, folderAndGroupMapper) })
}