package ru.tensor.sbis.design.period_picker.view.content_creator

import android.content.Context
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.container.ContentCreator
import ru.tensor.sbis.design.container.FragmentContent
import ru.tensor.sbis.design.container.SbisContainerImpl
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCountersRepository
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCustomTheme
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayType
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.MonthModePeriodPickerFragment
import java.util.Calendar

/**
 * Реализация [ContentCreator] для контейнера.
 *
 * @author mb.kruglova
 */
@Parcelize
internal class CompactPeriodPickerContainerContentCreator(
    private val startValue: Calendar?,
    private val endValue: Calendar?,
    private val isEnabled: Boolean,
    private val selectionType: SbisPeriodPickerSelectionType,
    private val dayType: SbisPeriodPickerDayType,
    private val displayedRange: SbisPeriodPickerRange,
    private val customView: ((Context) -> View)?,
    private val isDayAvailable: ((Calendar) -> Boolean)?,
    private val customDayTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme),
    private val isBottomPosition: Boolean,
    private val heightPercent: Int,
    private val anchorDate: Calendar?,
    private val requestKey: String,
    private val resultKey: String,
    private val factory: SbisPeriodPickerDayCountersRepository.Factory?
) : ContentCreator<FragmentContent>, Parcelable {
    override fun createContent(): FragmentContent {
        return object : FragmentContent {
            override fun getFragment(containerFragment: SbisContainerImpl): Fragment =
                MonthModePeriodPickerFragment.create(
                    startValue,
                    endValue,
                    isEnabled,
                    selectionType,
                    dayType,
                    displayedRange,
                    requestKey,
                    resultKey,
                    customView,
                    isDayAvailable = isDayAvailable,
                    dayCustomTheme = customDayTheme,
                    isBottomPosition = isBottomPosition,
                    anchorDate = anchorDate,
                    periodPickerHeightPercent = heightPercent,
                    factory = factory
                )

            override fun onRestoreFragment(containerFragment: SbisContainerImpl, fragment: Fragment) = Unit

            override fun useDefaultHorizontalOffset(): Boolean = false

            override fun customWidth(): Int = R.dimen.period_picker_width
        }
    }
}