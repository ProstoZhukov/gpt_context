package ru.tensor.sbis.deals.feature

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import java.util.UUID

/**
 * Облегчённая модель карточки сделки, предназначенная для передачи через аргументы.
 * @property extId идентификатор сделки (идентификаторВИ).
 * @property uuid идентификатор сделки (идентификатор).
 * @property type тип документа.
 * @property client клиент, который можно показать пользователю.
 * @property responsibleData ответственный, если нет ответственного - null.
 * @property regulation название регламента.
 * @property isImportant true - установлен флаг важности, false - нет.
 *
 * @author aa.sviridov
 */
@Parcelize
class DealMainDetails(
    val extId: String,
    val uuid: UUID,
    val type: String,
    val client: Client?,
    val responsibleData: PhotoData?,
    val regulation: String,
    val isImportant: Boolean,
) : Parcelable {

    @Parcelize
    data class Client(
        val uuid: UUID,
        val name: String,
    ) : Parcelable
}