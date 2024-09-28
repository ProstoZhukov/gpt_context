package ru.tensor.sbis.design.period_picker.view.content_creator

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCountersRepository
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCustomTheme
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayType
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.MonthModePeriodPickerFragment
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import java.util.Calendar

/**
 * Реализация [ContentCreatorParcelable] для шторки.
 *
 * @author mb.kruglova
 */
@Parcelize
internal class CompactPeriodPickerContentCreator(
    private val startValue: Calendar?,
    private val endValue: Calendar?,
    private val isEnabled: Boolean,
    private val selectionType: SbisPeriodPickerSelectionType,
    private val dayType: SbisPeriodPickerDayType,
    private val displayedRange: SbisPeriodPickerRange,
    private val customView: ((Context) -> View)?,
    private val isDayAvailable: ((Calendar) -> Boolean)?,
    private val dayCustomTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme),
    private val isBottomPosition: Boolean,
    private val anchorDate: Calendar?,
    private val requestKey: String,
    private val resultKey: String,
    private val factory: SbisPeriodPickerDayCountersRepository.Factory?
) : ContentCreatorParcelable {

    override fun createFragment(): Fragment = MonthModePeriodPickerFragment.create(
        startValue,
        endValue,
        isEnabled,
        selectionType,
        dayType,
        displayedRange,
        requestKey,
        resultKey,
        customView,
        anchorDate = anchorDate,
        isDayAvailable = isDayAvailable,
        dayCustomTheme = dayCustomTheme,
        isBottomPosition = isBottomPosition,
        factory = factory
    )
}