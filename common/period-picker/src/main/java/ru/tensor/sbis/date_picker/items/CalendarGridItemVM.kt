package ru.tensor.sbis.date_picker.items

import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import ru.tensor.sbis.date_picker.R

/**
 * Базовый класс для ячеек календарной сетки (месяцев и дней)
 * @property defaultBackgroundRes идентификатор фона вьюшки номера дня по-умолчанию
 *
 * @author mb.kruglova
 */
abstract class CalendarGridItemVM(@DrawableRes protected val defaultBackgroundRes: Int) {

    /** true если вьюшка может кликаться, false - нет */
    var clickable = ObservableBoolean(true)
        private set

    /** Идентификатор фона вьюшки номера дня */
    var backgroundIdRes = ObservableInt(defaultBackgroundRes)
        private set

    /** Идентификатор аттрибута цвета текста номера дня */
    var textColorResAttr = ObservableInt(R.attr.date_picker_items_text_color)
        private set

    /**
     * Установка маркера выбранности элемента периода
     */
    open fun setSelectionMarker() {
        backgroundIdRes.set(R.drawable.date_picker_item_selected_background)
    }

    /**
     * Установка маркера начала периода
     */
    open fun setStartSelectionMarker() {
        backgroundIdRes.set(R.drawable.date_picker_item_start_selection_background)
    }

    /**
     * Установка маркера конца периода
     */
    open fun setEndSelectionMarker() {
        backgroundIdRes.set(R.drawable.date_picker_item_end_selection_background)
    }

    /**
     * Установка маркера периода, состоящего из одного дня/месяца
     */
    open fun setStartEndSelectionMarker() {
        backgroundIdRes.set(R.drawable.date_picker_item_start_end_selection_background)
    }

    /**
     * Сброс маркера выбранности
     */
    open fun resetSelectionMarker() {
        backgroundIdRes.set(defaultBackgroundRes)
    }

    /**
     * Установка цвета текста для выходного дня
     */
    open fun setHolidayColor() {
        textColorResAttr.set(R.attr.date_picker_weekend_color)
    }

    /**
     * Установка внешнего вида для текущего дня/месяца
     */
    open fun setCurrentDateAppearance() {
        textColorResAttr.set(R.attr.date_picker_current_month_color)
    }

    /**
     * Отметка недоступной ячейки
     */
    open fun setUnavailable() {
        textColorResAttr.set(R.attr.date_picker_unavailable_color)
        clickable.set(false)
    }

    /**
     * Функция слияния ViewModel-ей. Похоже на схему ViewModelMerge во ViewModelAdapter.
     * Требуется чтобы DataBinding "не терял" свой ObservableField и его сеттер работал.
     */
    @CallSuper
    open fun merge(other: CalendarGridItemVM) {
        clickable = other.clickable
        backgroundIdRes = other.backgroundIdRes
        textColorResAttr = other.textColorResAttr
    }
}