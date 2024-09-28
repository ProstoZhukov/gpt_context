package ru.tensor.sbis.date_picker.selection

import ru.tensor.sbis.date_picker.Period
import ru.tensor.sbis.date_picker.PeriodHelper
import ru.tensor.sbis.date_picker.PickerType
import ru.tensor.sbis.date_picker.Validator
import ru.tensor.sbis.date_picker.items.CalendarVmStorage
import ru.tensor.sbis.date_picker.items.NamedItemVM
import ru.tensor.sbis.date_picker.items.SpecialDates

/**
 * @author mb.kruglova
 */
class SelectionStrategyFactory(private val periodHelper: PeriodHelper, private val validator: Validator) {

    fun create(
        type: PickerType,
        period: Period,
        storage: CalendarVmStorage,
        saveAndExit: (Period) -> Unit,
        selectedPeriodChanged: (Period) -> Unit,
        selectedNamedItemChanged: (NamedItemVM?) -> Unit,
        specialDates: SpecialDates
    ): SelectionStrategy {
        return when (type) {
            PickerType.PERIOD -> PeriodSelectionStrategy(
                period,
                storage,
                periodHelper,
                selectedPeriodChanged,
                selectedNamedItemChanged,
                validator,
                specialDates
            )
            PickerType.DATE -> DateSelectionStrategy(
                period,
                storage,
                periodHelper,
                selectedPeriodChanged,
                selectedNamedItemChanged
            )
            PickerType.PERIOD_BY_ONE_CLICK,
            PickerType.PERIOD_ONLY -> PeriodByOneClickSelectionStrategy(
                period,
                storage,
                periodHelper,
                selectedPeriodChanged,
                selectedNamedItemChanged
            )
            PickerType.DATE_ONCE -> DateOnceSelectionStrategy(
                period,
                storage,
                periodHelper,
                saveAndExit,
                selectedPeriodChanged,
                selectedNamedItemChanged
            )
            PickerType.MONTH_ONCE -> MonthOnceSelectionStrategy(
                period,
                storage,
                periodHelper,
                saveAndExit,
                selectedPeriodChanged,
                selectedNamedItemChanged
            )
        }
    }
}