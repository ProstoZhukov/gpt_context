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
class MonthOnceSelectionStrategy(
    period: Period,
    storage: CalendarVmStorage,
    periodHelper: PeriodHelper,
    saveAndExit: (Period) -> Unit,
    selectedPeriodChanged: (Period) -> Unit,
    selectedNamedItemChanged: (NamedItemVM?) -> Unit
) : SelectionStrategy(period, storage, periodHelper, selectedPeriodChanged, selectedNamedItemChanged) {

    override fun getItemClickedPeriod(key: PeriodsVMKey, selected: Int, itemPeriod: Period) = itemPeriod

    // для типа компонента [PickerType.MONTH_ONCE] выбор периода завершается закрытием окна
    override val itemClickedAction: (() -> Unit)? = { saveAndExit(selectedPeriod) }

    override val halfYearClicked: ((PeriodsVMKey, Int, NamedItemVM) -> Unit)? = null

    override val quarterClicked: ((PeriodsVMKey, Int, NamedItemVM) -> Unit)? = null

    override val dayClicked: ((PeriodsVMKey, Int) -> Unit)? = null

    override val monthLabelClicked: ((PeriodsVMKey, NamedItemVM, Calendar, Calendar) -> Unit)? = null
}