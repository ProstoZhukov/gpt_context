/**
 * Модели результата выбора получателей.
 *
 * @author vv.chekurda
 */
@file:Suppress("DEPRECATION")
package ru.tensor.sbis.design.recipient_selection.domain.factory.result

import kotlinx.parcelize.Parcelize
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientDepartment
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientPerson
import ru.tensor.sbis.person_decl.profile.model.Gender
import ru.tensor.sbis.person_decl.profile.model.InitialsStubData
import ru.tensor.sbis.person_decl.profile.model.PersonName
import java.util.UUID

/**
 * Модель персоны получателя.
 * @see RecipientPerson
 */
@Parcelize
internal data class RecipientPersonModel(
    override val uuid: UUID,
    override val faceId: Long?,
    override val name: PersonName,
    override val photoUrl: String? = null,
    override val gender: Gender = Gender.UNKNOWN,
    override val initialsStubData: InitialsStubData? = null,
    override val companyOrDepartment: String = StringUtils.EMPTY,
    override val inMyCompany: Boolean = false,
    override val position: String? = null,
    override val hasAccess: Boolean = false
) : RecipientPerson {

    override val title: String
        get() = name.fullName
}

/**
 * Модель подразделения получателя.
 * @see RecipientDepartment
 */
@Parcelize
internal data class RecipientDepartmentModel(
    override val uuid: UUID,
    override val faceId: Long?,
    override val name: String,
    override val chief: String,
    override val employeeCount: Int
) : RecipientDepartment {

    override val title: String
        get() = name
}