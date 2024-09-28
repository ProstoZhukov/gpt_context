package ru.tensor.sbis.date_picker.selection

import ru.tensor.sbis.date_picker.Period
import ru.tensor.sbis.date_picker.PeriodHelper
import ru.tensor.sbis.date_picker.PeriodsVMKey
import ru.tensor.sbis.date_picker.items.CalendarVmStorage
import ru.tensor.sbis.date_picker.items.NamedItemVM

/**
 * @author mb.kruglova
 */
class PeriodByOneClickSelectionStrategy(
    period: Period,
    storage: CalendarVmStorage,
    periodHelper: PeriodHelper,
    selectedPeriodChanged: (Period) -> Unit,
    selectedNamedItemChanged: (NamedItemVM?) -> Unit
) : SelectionStrategy(period, storage, periodHelper, selectedPeriodChanged, selectedNamedItemChanged) {

    override fun getItemClickedPeriod(key: PeriodsVMKey, selected: Int, itemPeriod: Period) = itemPeriod
}