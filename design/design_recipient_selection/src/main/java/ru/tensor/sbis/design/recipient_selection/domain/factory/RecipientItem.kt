/**
 * Внутренние модели реализации элементов компонента выбора получателей.
 *
 * @author vv.chekurda
 */
package ru.tensor.sbis.design.recipient_selection.domain.factory

import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.common_views.SearchSpan
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientDepartmentId
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientPersonId
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.data.SelectionPersonItem
import ru.tensor.sbis.persons.PersonName

/**
 * Элемент списка компонента выбора получателей.
 */
internal sealed interface RecipientItem : SelectionItem

/**
 * Элемент персоны.
 * @see SelectionPersonItem
 *
 * @property faceId идентификатор лица.
 */
@Parcelize
internal data class RecipientPersonItem(
    override val id: RecipientPersonId,
    override val title: String,
    override val subtitle: String?,
    override val photoData: PersonData,
    override val personName: PersonName,
    override val isInMyCompany: Boolean,
    override val position: String?,
    override val titleHighlights: List<SearchSpan> = emptyList(),
    val faceId: Long? = null
) : RecipientItem, SelectionPersonItem {

    override val clearedHighlights: SelectionItem
        get() = this.copy(titleHighlights = emptyList())
}

/**
 * Элемент подразделения.
 * @see SelectionFolderItem
 *
 * @property faceId идентификатор лица.
 */
@Parcelize
internal data class RecipientDepartmentItem(
    override val id: RecipientDepartmentId,
    override val title: String,
    override val photoData: PhotoData? = null,
    override val subtitle: String? = null,
    override val counter: Int? = null,
    override val openable: Boolean = true,
    override val selectable: Boolean = true,
    override val titleHighlights: List<SearchSpan> = emptyList(),
    val faceId: Long? = null
) : RecipientItem, SelectionFolderItem {

    override val clearedHighlights: SelectionItem
        get() = this.copy(titleHighlights = emptyList())
}