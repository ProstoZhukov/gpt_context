package ru.tensor.sbis.design.period_picker.feature

import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import ru.tensor.sbis.design.period_picker.decl.SbisCompactPeriodPickerFragmentFeature
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCountersRepository
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCustomTheme
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayType
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.MonthModePeriodPickerFragment
import ru.tensor.sbis.design.period_picker.view.utils.getFirstDisplayedRange
import java.util.Calendar

/**
 * Реализация фичи компонента Выбор периода для отображения его как фрагмент [SbisCompactPeriodPickerFragmentFeature].
 *
 * @author mb.kruglova
 */
class SbisCompactPeriodPickerFragmentFeatureImpl : SbisCompactPeriodPickerFragmentFeature {

    companion object {
        private const val COMPACT_PERIOD_PICKER_FRAGMENT = "COMPACT_PERIOD_PICKER_FRAGMENT"
    }

    override fun showCompactPeriodPicker(
        host: Fragment,
        containerId: Int,
        startValue: Calendar?,
        endValue: Calendar?,
        isEnabled: Boolean,
        selectionType: SbisPeriodPickerSelectionType,
        dayType: SbisPeriodPickerDayType,
        displayedRanges: List<SbisPeriodPickerRange>?,
        isDayAvailable: ((Calendar) -> Boolean)?,
        dayCustomTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme),
        isBottomPosition: Boolean,
        anchorDate: Calendar?,
        requestKey: String,
        resultKey: String,
        dayCountersRepFactory: SbisPeriodPickerDayCountersRepository.Factory?
    ) {
        val fragment = host.childFragmentManager.findFragmentByTag(COMPACT_PERIOD_PICKER_FRAGMENT)
        if (fragment != null) {
            host.childFragmentManager.beginTransaction().remove(fragment).commit()
        }

        host.childFragmentManager.commit {
            add(
                containerId,
                MonthModePeriodPickerFragment.create(
                    startValue = startValue,
                    endValue = endValue,
                    isEnabled = isEnabled,
                    selectionType = if (isDayAvailable != null) SbisPeriodPickerSelectionType.Single else selectionType,
                    dayType = dayType,
                    displayedRange = getFirstDisplayedRange(displayedRanges),
                    isDayAvailable = isDayAvailable,
                    dayCustomTheme = dayCustomTheme,
                    isBottomPosition = isBottomPosition,
                    anchorDate = anchorDate,
                    isFragment = true,
                    requestKey = requestKey,
                    resultKey = resultKey,
                    factory = dayCountersRepFactory
                ),
                COMPACT_PERIOD_PICKER_FRAGMENT
            )
        }
    }
}