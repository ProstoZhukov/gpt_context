package ru.tensor.sbis.date_picker.selection

import ru.tensor.sbis.date_picker.Period
import ru.tensor.sbis.date_picker.PeriodHelper
import ru.tensor.sbis.date_picker.PeriodsVMKey
import ru.tensor.sbis.date_picker.items.CalendarVmStorage
import ru.tensor.sbis.date_picker.items.NamedItemVM
import java.util.*

/**
 * @author mb.kruglova
 */
open class DateSelectionStrategy(
    period: Period,
    storage: CalendarVmStorage,
    periodHelper: PeriodHelper,
    selectedPeriodChanged: (Period) -> Unit,
    selectedNamedItemChanged: (NamedItemVM?) -> Unit
) : SelectionStrategy(period, storage, periodHelper, selectedPeriodChanged, selectedNamedItemChanged) {

    override fun getItemClickedPeriod(key: PeriodsVMKey, selected: Int, itemPeriod: Period) = itemPeriod

    override val dateSelection = true

    /**
     * Обрабатывается только нажатие на день
     */
    override val yearClicked: ((PeriodsVMKey, NamedItemVM) -> Unit)? = null

    override val halfYearClicked: ((PeriodsVMKey, Int, NamedItemVM) -> Unit)? = null

    override val quarterClicked: ((PeriodsVMKey, Int, NamedItemVM) -> Unit)? = null

    override val monthClicked: ((PeriodsVMKey, Int) -> Unit)? = null

    override val monthLabelClicked: ((PeriodsVMKey, NamedItemVM, Calendar, Calendar) -> Unit)? = null
}