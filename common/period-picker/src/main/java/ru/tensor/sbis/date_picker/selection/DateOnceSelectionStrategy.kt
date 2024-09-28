package ru.tensor.sbis.date_picker.selection

import ru.tensor.sbis.date_picker.Period
import ru.tensor.sbis.date_picker.PeriodHelper
import ru.tensor.sbis.date_picker.items.CalendarVmStorage
import ru.tensor.sbis.date_picker.items.NamedItemVM

/**
 * @author mb.kruglova
 */
class DateOnceSelectionStrategy(
    period: Period,
    storage: CalendarVmStorage,
    periodHelper: PeriodHelper,
    saveAndExit: (Period) -> Unit,
    selectedPeriodChanged: (Period) -> Unit,
    selectedNamedItemChanged: (NamedItemVM?) -> Unit
) : DateSelectionStrategy(period, storage, periodHelper, selectedPeriodChanged, selectedNamedItemChanged) {

    // для типа компонента [PickerType.DATE_ONCE] выбор даты завершается закрытием окна
    override val itemClickedAction: (() -> Unit)? = { saveAndExit(selectedPeriod) }
}

