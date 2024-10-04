package ru.tensor.sbis.hallscheme.v2.business.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.hallscheme.R

/**
 * Статус объекта.
 * @author aa.gulevskiy
 */
sealed class TableStatus : Parcelable {
    /**
     * Ссылка на атрибут цвета.
     */
    internal abstract val attr: Int

    /**
     * Есть готовые блюда.
     */
    @Parcelize
    object HasReadyDishes : TableStatus() {
        override val attr: Int
            get() = R.attr.hall_scheme_table_occupied_contour
    }

    /**
     * Занят (есть заказы в работе).
     */
    @Parcelize
    object Occupied : TableStatus() {
        override val attr: Int
            get() = R.attr.hall_scheme_table_occupied_contour
    }

    /**
     * Пустой стол без заказов.
     */
    @Parcelize
    object Default : TableStatus() {
        override val attr: Int
            get() = R.attr.hall_scheme_table_empty_contour
    }

    /**
     * Забронирован.
     */
    @Parcelize
    object OccupiedForBooking : TableStatus() {
        override val attr: Int
            get() = R.attr.hall_scheme_table_empty_contour
    }

    /**
     * Недоступен.
     */
    @Parcelize
    object Disabled : TableStatus() {
        override val attr: Int
            get() = R.attr.hall_scheme_table_empty_contour
    }

    /**
     * Занят для конкретного гостя.
     */
    @Parcelize
    object OccupiedForUser : TableStatus() {
        override val attr: Int
            get() = R.attr.hall_scheme_table_occupied_contour
    }
}