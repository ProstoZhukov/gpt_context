package ru.tensor.sbis.communication_decl.employeeselection.result

import android.os.Parcelable
import java.util.UUID

/**
 * Данные выбранного item'а для списка получателей репоста новости
 */
interface RecipientSelectionResultDataForRepostContract : Parcelable {
    val uuid: UUID?
    val title: String
    val subtitle: String?
    val imageUrl: String?
    val hasNestedItems: Boolean
    val counter: Int
    val type: RecipientSelectionForRepostItemType
}
