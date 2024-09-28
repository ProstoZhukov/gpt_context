package ru.tensor.sbis.design.selection.ui.model.recipient

import androidx.annotation.IntRange
import ru.tensor.sbis.design.profile_decl.person.CompanyData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemId
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.persons.PersonName

/**
 * Реализация по умолчанию для выбора получателей
 *
 * @author ma.kolpakov
 */
sealed class DefaultRecipientSelectorItemModel : RecipientSelectorItemModel {
    override lateinit var meta: SelectorItemMeta
}

/**
 * Реализация по умолчанию для модели персоны
 */
data class DefaultPersonSelectorItemModel @JvmOverloads constructor(
    override val id: SelectorItemId,
    override val title: String,
    override val subtitle: String? = null,
    override val personData: PersonData,
    override val personName: PersonName
) : DefaultRecipientSelectorItemModel(), PersonSelectorItemModel {

    constructor(
        id: SelectorItemId,
        title: String,
        subtitle: String? = null,
        personData: PersonData,
        firstName: String,
        lastName: String
    ) : this(
        id, title, subtitle, personData, PersonName(firstName, lastName, "")
    )
}

/**
 * Реализация по умолчанию для модели группы (соц. сети)
 */
data class DefaultGroupSelectorItemModel @JvmOverloads constructor(
    override val id: SelectorItemId,
    override val title: String,
    override val subtitle: String? = null,
    override val imageUri: String,
    @IntRange(from = 0)
    override val membersCount: Int
) : DefaultRecipientSelectorItemModel(), GroupSelectorItemModel

/**
 * Реализация по умолчанию для модели рабочей группы
 */
data class DefaultDepartmentSelectorItemModel @JvmOverloads constructor(
    override val id: SelectorItemId,
    override val title: String,
    override val subtitle: String? = null,
    @IntRange(from = 0)
    override val membersCount: Int
) : DefaultRecipientSelectorItemModel(), DepartmentSelectorItemModel

/**
 * Реализация по умолчанию для модели контрагента
 *
 * @author us.bessonov
 */
class DefaultContractorSelectorItemModel(
    override val id: SelectorItemId,
    override val title: String,
    override val subtitle: String?,
    override val photoData: CompanyData
) : DefaultRecipientSelectorItemModel(), ContractorSelectorItemModel