package ru.tensor.sbis.hallscheme.v2.business.model.tableinfo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.hallscheme.v2.business.model.TableStatus
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.Booking
import java.util.UUID

/**
 * Модель, представляющая данные по столу.
 */
@Parcelize
data class TableInfo(
    var totalPlaces: Int = 0,
    val totalSum: Double? = null,
    val maxDishLatency: Long = 0,
    val billNumber: Int = 0,
    val bookings: List<Booking> = emptyList(),
    val dishesNumber: String? = null,
    val tableStatus: TableStatus = TableStatus.Default,
    val tableOutlines: List<TableOutline> = emptyList(),
    val bellCount: Short = 0,
    val showCallButton: Boolean = false,
    val payment: Payment? = null,
    val assignmentInfo: AssignmentInfo? = null
) : Parcelable

/**
 * Модель для отображения оплаты заказа на столе.
 * @param partial Если есть частично оплаченный заказ.
 */
@Parcelize
data class Payment(val partial: Boolean): Parcelable

/**
 * Модель для отображения закреплённого за столом пользователя.
 * @param isMy true, если стол закреплён за текущим пользователем. В этом случае выводится символ "Я".
 * @param id Идентификатор закреплённого пользователя.
 * @param fullName Имя закреплённого пользователя.
 * @param photoUri Ссылка на фотографию закреплённого пользователя.
 */
@Parcelize
data class AssignmentInfo(
    val isMy: Boolean,
    val id: UUID,
    val fullName: String,
    val photoUri: String?
): Parcelable