package ru.tensor.sbis.design.period_picker.view.period_picker.details.model

import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCountersRepository
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCustomTheme
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.view.models.MarkerType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.domain.CalendarStorageRepository
import java.util.Calendar

/**
 * Параметры для намерений.
 *
 * @author mb.kruglova
 */
internal data class IntentParams(
    val repository: CalendarStorageRepository,
    val markerType: MarkerType,
    val isCompact: Boolean,
    val dayCountersRepository: SbisPeriodPickerDayCountersRepository?,
    val displayedRange: SbisPeriodPickerRange,
    val isDayAvailable: ((Calendar) -> Boolean)?,
    val isFragment: Boolean,
    val dayCustomTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme)
)