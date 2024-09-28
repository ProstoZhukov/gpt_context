package ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.vm.data

import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.common_views.SearchSpan
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientPersonId
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design_selection.contract.data.SelectionPersonItem
import ru.tensor.sbis.persons.PersonName

/**
 * Модель персоны для функции direct share.
 * @see SelectionPersonItem
 *
 * @author vv.chekurda.
 */
@Parcelize
internal data class DirectSharePerson(
    override val id: RecipientPersonId,
    override val title: String,
    override val subtitle: String?,
    override val photoData: PersonData,
    override val isInMyCompany: Boolean,
    override val personName: PersonName
) : SelectionPersonItem {
    override val titleHighlights: List<SearchSpan>
        get() = emptyList()
    override val position: String?
        get() = null
}