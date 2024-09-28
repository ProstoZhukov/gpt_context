package ru.tensor.sbis.date_picker.year.items

import androidx.databinding.ObservableBoolean
import ru.tensor.sbis.date_picker.PeriodsVM
import ru.tensor.sbis.date_picker.items.NamedItemVM

/**
 * Основной элемент календарной сетки режима "Год".
 * Внутри себя содержит заголовок и ячейки полугодий, кварталов, месяцев
 *
 * @author mb.kruglova
 */
data class YearPeriodsVM(
    override val label: String,
    override val items: List<MonthVM>,
    val labelClickable: ObservableBoolean,
    val halfYear: List<HalfYearVM>?,
    val quarters: List<QuarterVM>?,
    private val yearClicked: (NamedItemVM) -> Unit,
    private val halfYearClicked: (Int, NamedItemVM) -> Unit,
    private val quarterClicked: (Int, NamedItemVM) -> Unit,
    private val monthClicked: (Int) -> Unit
) : PeriodsVM(label, items) {

    init {
        items.forEachIndexed { index, month -> month.clickAction = { monthClicked(index) } }
        halfYear?.forEachIndexed { index, halfYear ->
            halfYear.clickAction = { halfYearClicked(index, halfYear) }
        }
        quarters?.forEachIndexed { index, quarter -> quarter.clickAction = { quarterClicked(index, quarter) } }
    }

    val hasHalfYear = halfYear?.isNotEmpty() ?: false
    val hasQuarters = quarters?.isNotEmpty() ?: false

    fun onYearClick() {
        yearClicked(this)
    }
}