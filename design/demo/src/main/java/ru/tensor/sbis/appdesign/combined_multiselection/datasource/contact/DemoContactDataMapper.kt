package ru.tensor.sbis.appdesign.combined_multiselection.datasource.contact

import ru.tensor.sbis.appdesign.combined_multiselection.data.contact.DemoContactServiceResult
import ru.tensor.sbis.design.profile.person.data.PersonData
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.DefaultPersonSelectorItemModel

/**
 * @author ma.kolpakov
 */
class DemoContactDataMapper : ListMapper<DemoContactServiceResult, SelectorItemModel> {

    override fun invoke(serviceData: DemoContactServiceResult): List<SelectorItemModel> =
        serviceData.data.map {
            DefaultPersonSelectorItemModel(
                id = it.id,
                title = it.title,
                subtitle = it.subtitle,
                personData = PersonData(photoUrl = it.photoUrl),
                firstName = "",
                lastName = "",
            )
        }
}
