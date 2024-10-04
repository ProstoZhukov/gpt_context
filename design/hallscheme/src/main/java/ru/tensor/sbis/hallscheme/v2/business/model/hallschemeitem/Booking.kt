package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.hallscheme.v2.business.BookingStatus
import java.util.Date

/**
 * Класс, представляющий информацию о бронировании объекта.
 * @param status статус брони [BookingStatus]
 * @param dateBooked время брони.
 * @param personsAmount Количество гостей.
 * @param intersect если true, то бронь по времени пересекается с другой бронью.
 */
@Parcelize
class Booking(
    val status: BookingStatus,
    val dateBooked: Date,
    val personsAmount: UInt,
    val intersect: Boolean
) : Parcelable