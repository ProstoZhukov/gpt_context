package ru.tensor.sbis.date_picker.month.items

import androidx.databinding.ObservableField
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import ru.tensor.sbis.date_picker.R
import ru.tensor.sbis.date_picker.items.CalendarGridItemVM
import ru.tensor.sbis.date_picker.items.Day

/**
 * Ячейка дня
 * @property dayOfMonth день месяца
 * @property dayOfWeek день недели
 * @property source описывает день в году
 * @property isCurrent true если день текущий, иначе false
 *
 * @author mb.kruglova
 */
class DayVM(
    val dayOfMonth: Int,
    val dayOfWeek: Int,
    val source: Day,
    private val isCurrent: Boolean
) : CalendarGridItemVM(R.drawable.date_picker_item_default_background) {

    init {
        if (isCurrent) {
            setCurrentDateAppearance()
        }
    }

    /** Действие по клику */
    var clickAction: ((DayVM) -> Unit)? = null

    /** Значение счётчика около даты */
    var counter = ObservableField("")
        private set

    /** true если день недоступен, иначе false */
    private var isUnavailable = false

    /** Вызывает действие по клику */
    fun onClick() {
        clickAction?.invoke(this)
    }

    override fun setSelectionMarker() {
        if (isCurrent) {
            // специфичный background для текущего дня
            backgroundIdRes.set(R.drawable.date_picker_item_selected_background_for_current_day)
        } else {
            super.setSelectionMarker()
        }
    }

    override fun setStartSelectionMarker() {
        if (isCurrent) {
            // специфичный background для текущего дня
            backgroundIdRes.set(R.drawable.date_picker_item_start_selection_background_for_current_day)
        } else {
            super.setStartSelectionMarker()
        }
    }

    override fun setEndSelectionMarker() {
        if (isCurrent) {
            // специфичный background для текущего дня
            backgroundIdRes.set(R.drawable.date_picker_item_end_selection_background_for_current_day)
        } else {
            super.setEndSelectionMarker()
        }
    }

    override fun setStartEndSelectionMarker() {
        if (isCurrent) {
            // специфичный background для текущего дня
            backgroundIdRes.set(R.drawable.date_picker_item_start_end_selection_background_for_current_day)
        } else {
            super.setStartEndSelectionMarker()
        }
    }

    override fun resetSelectionMarker() {
        if (isCurrent) {
            // установка background для текущего дня по умолчанию
            backgroundIdRes.set(R.drawable.date_picker_current_day_background)
        } else {
            super.resetSelectionMarker()
        }
    }

    override fun setHolidayColor() {
        // текущий день не отмечается как выходной или праздничный
        if (!isCurrent && !isUnavailable) {
            super.setHolidayColor()
        }
    }

    override fun setCurrentDateAppearance() {
        backgroundIdRes.set(R.drawable.date_picker_current_day_background)
        textColorResAttr.set(R.attr.date_picker_current_day_text_color)
    }

    override fun setUnavailable() {
        isUnavailable = true
        super.setUnavailable()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DayVM

        return EqualsBuilder()
            .append(defaultBackgroundRes, other.defaultBackgroundRes)
            .append(dayOfMonth, other.dayOfMonth)
            .append(dayOfWeek, other.dayOfWeek)
            .append(source, other.source)
            .append(isCurrent, other.isCurrent)
            .append(backgroundIdRes.get(), other.backgroundIdRes.get())
            .append(textColorResAttr.get(), other.textColorResAttr.get())
            .append(clickable.get(), other.clickable.get())
            .append(counter.get(), other.counter.get())
            .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder()
            .append(defaultBackgroundRes)
            .append(dayOfMonth)
            .append(dayOfWeek)
            .append(source)
            .append(isCurrent)
            .append(backgroundIdRes.get())
            .append(textColorResAttr.get())
            .append(clickable.get())
            .append(counter.get())
            .toHashCode()
    }

    override fun merge(other: CalendarGridItemVM) {
        super.merge(other)
        if (other !is DayVM) return
        counter = other.counter
    }
}