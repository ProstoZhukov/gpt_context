package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.mapper

import ru.tensor.sbis.communicator.crm.conversation.R
import ru.tensor.sbis.consultations.generated.OperatorViewModel
import ru.tensor.sbis.crud3.domain.ItemInSectionMapper
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.list.view.binding.BindingItem
import ru.tensor.sbis.list.view.binding.DataBindingViewHolderHelper
import ru.tensor.sbis.list.view.binding.LayoutIdViewFactory
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.item.Options
import ru.tensor.sbis.profiles.generated.PersonDecoration
import java.util.UUID

/**
 * Реализация ItemMapper для crud3.
 *
 * @author da.zhukov.
 */
internal class CRMAnotherOperatorMapper : ItemInSectionMapper<OperatorViewModel, AnyItem> {

    override fun map(
        item: OperatorViewModel,
        defaultClickAction: (OperatorViewModel) -> Unit
    ): AnyItem {
        val personData = item.employeeProfileModel?.person?.photoDecoration?.let {
            createPersonData(
                item.id,
                item.employeeProfileModel?.person?.photoUrl,
                it
            )
        }
        return BindingItem(
            data = CRMAnotherOperatorViewModelBinding(
                operatorId = item.id,
                name = item.name,
                consultationsCount = item.consultationsCount.toString(),
                position = item.employeeProfileModel?.position,
                inMyCompany = item.employeeProfileModel?.inMyCompany,
                companyOrDepartment = item.employeeProfileModel?.companyOrDepartment,
                photoDecoration = item.employeeProfileModel?.person?.photoDecoration,
                personData = personData
            ),
            dataBindingViewHolderHelper = DataBindingViewHolderHelper(
                factory = LayoutIdViewFactory(R.layout.communicator_crm_another_operator_item)
            ),
            options = Options(
                clickAction = {
                    defaultClickAction(item)
                },
                customBackground = true,
                customSidePadding = true
            )
        )
    }

    private fun createPersonData(operatorId: UUID, photoUrl: String?, personDecoration: PersonDecoration): PersonData {
        val initials = personDecoration.initials
        val backgroundColorHex = personDecoration.backgroundColorHex
        val initialsStubData = InitialsStubData(
            initials,
            backgroundColorHex
        )
        return PersonData(operatorId, photoUrl, initialsStubData)
    }
}