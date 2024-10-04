package ru.tensor.sbis.design.message_panel.decl.recipients.data

import ru.tensor.sbis.persons.IContactVM
import java.io.Serializable
import java.util.*

/**
 * Интерфейс элемента списка получателей панели сообщений.
 *
 * @author dv.baranov
 */
sealed interface RecipientItem : Serializable {
    val uuid: UUID
}

/**
 * Модель персоны.
 */
data class RecipientPersonItem(
    val personModel: IContactVM
) : RecipientItem,
    IContactVM by personModel {
    override val uuid: UUID = personModel.uuid
}

/**
 * Модель подразделения.
 */
data class RecipientDepartmentItem(
    override val uuid: UUID,
    val name: String,
    val personModels: List<IContactVM>
) : RecipientItem