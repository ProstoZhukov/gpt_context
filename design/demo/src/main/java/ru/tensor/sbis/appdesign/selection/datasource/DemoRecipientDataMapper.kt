package ru.tensor.sbis.appdesign.selection.datasource

import ru.tensor.sbis.appdesign.selection.data.DemoRecipientServiceResult
import ru.tensor.sbis.appdesign.selection.data.RecipientType.*
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.model.recipient.DefaultDepartmentSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.DefaultGroupSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.DefaultPersonSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.DefaultRecipientSelectorItemModel

/**
 * @author ma.kolpakov
 */
class DemoRecipientDataMapper : ListMapper<DemoRecipientServiceResult, DefaultRecipientSelectorItemModel> {

    override fun invoke(serviceData: DemoRecipientServiceResult): List<DefaultRecipientSelectorItemModel> =
        mapServiceData(serviceData)

    fun mapServiceData(serviceData: DemoRecipientServiceResult): List<DefaultRecipientSelectorItemModel> {
        return serviceData.data.map {
            with(it) {
                when (it.recipientType) {
                    PERSON     -> DefaultPersonSelectorItemModel(
                        id.toString(),
                        title,
                        subtitle,
                        personData!!,
                        firstName!!,
                        lastName!!
                    )
                    DEPARTMENT      -> DefaultDepartmentSelectorItemModel(
                        id.toString(),
                        title,
                        subtitle,
                        membersCount
                    )
                    GROUP -> DefaultGroupSelectorItemModel(
                        id.toString(),
                        title,
                        subtitle,
                        imageUrl!!,
                        membersCount
                    )
                }
            }
        }
    }
}