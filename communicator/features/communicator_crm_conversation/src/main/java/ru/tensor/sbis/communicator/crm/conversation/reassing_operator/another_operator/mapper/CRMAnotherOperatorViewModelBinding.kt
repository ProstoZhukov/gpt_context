package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.mapper

import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.list.view.item.comparator.ComparableItem
import ru.tensor.sbis.profiles.generated.PersonDecoration
import java.util.UUID

/**
 * Модель данных, представление OperatorViewModel.
 * Необходима для упрощения доступа к полям исходной OperatorViewModel, а также для возможности расширения.
 *
 * @author da.zhukov
 */
data class CRMAnotherOperatorViewModelBinding(
    val operatorId: UUID,
    val name: String,
    val consultationsCount: String,
    val position: String?,
    val inMyCompany: Boolean?,
    val companyOrDepartment: String?,
    val photoDecoration: PersonDecoration?,
    val personData: PersonData?
) : ComparableItem<CRMAnotherOperatorViewModelBinding> {

    override fun areTheSame(otherItem: CRMAnotherOperatorViewModelBinding): Boolean = operatorId == otherItem.operatorId
}