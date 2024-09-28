package ru.tensor.sbis.date_picker.selection

import ru.tensor.sbis.date_picker.Period
import ru.tensor.sbis.date_picker.PeriodHelper
import ru.tensor.sbis.date_picker.PeriodsVMKey
import ru.tensor.sbis.date_picker.Validator
import ru.tensor.sbis.date_picker.items.CalendarVmStorage
import ru.tensor.sbis.date_picker.items.NamedItemVM
import ru.tensor.sbis.date_picker.items.SpecialDates

/**
 * @author mb.kruglova
 */
class PeriodSelectionStrategy(
    period: Period,
    storage: CalendarVmStorage,
    periodHelper: PeriodHelper,
    selectedPeriodChanged: (Period) -> Unit,
    selectedNamedItemChanged: (NamedItemVM?) -> Unit,
    private val validator: Validator,
    private val specialDates: SpecialDates
) : SelectionStrategy(period, storage, periodHelper, selectedPeriodChanged, selectedNamedItemChanged) {

    override fun getItemClickedPeriod(key: PeriodsVMKey, selected: Int, itemPeriod: Period): Period {
        return when {
            selectedPeriod.hasFromAndTo -> Period.createFrom(key, selected)
            selectedPeriod.hasFrom -> {
                val period = Period.createFromAndTo(selectedPeriod, key, selected)
                validator.excludeUnavailableDays(period, specialDates.unavailableDays)
            }
            else -> Period.createFrom(key, selected)
        }
    }
}