package ru.tensor.sbis.recipient_selection.profile.data.factory_models.single

import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.model.recipient.RecipientSelectorItemModel
import ru.tensor.sbis.recipient_selection.profile.data.PersonSelectorItemModelImpl
import ru.tensor.sbis.recipient_selection.profile.data.factory_models.ProfilesFoldersResult
import ru.tensor.sbis.recipient_selection.profile.mapper.ProfileAndContactItemMapper
import ru.tensor.sbis.recipient_selection.profile.ui.mapPersonDecorationToInitialsStubData

/**
 * Маппер списка получателей в модели компонента селектора
 *
 * @author vv.chekurda
 */
internal class SingleSelectionMapper(private val profileAndContactMapper: ProfileAndContactItemMapper) :
    ListMapper<ProfilesFoldersResult, RecipientSelectorItemModel> {

    override fun invoke(result: ProfilesFoldersResult): List<RecipientSelectorItemModel> =
        result.profiles.map { profile ->
            val personData = PersonData(
                profile.person.uuid,
                profile.person.photoUrl,
                profile.person.photoDecoration.mapPersonDecorationToInitialsStubData()
            )
            PersonSelectorItemModelImpl(profile, profileAndContactMapper, personData)
        }
}