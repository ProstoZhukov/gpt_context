package ru.tensor.sbis.communication_decl.selection.recipient.data

import android.os.Parcelable
import java.util.UUID

/**
 * Интерфейс подразделения с получателями.
 *
 * @see RecipientSelectionItem
 *
 * @property faceId идентификатор лица.
 * @property name название подразделения.
 * @property chief начальник подразделения.
 * @property employeeCount количество сотрудников в подразделении.
 *
 * @author vv.chekurda
 */
interface RecipientDepartment : RecipientSelectionItem, Parcelable {
    override val uuid: UUID
    val faceId: Long?
    val name: String
    val chief: String
    val employeeCount: Int
}