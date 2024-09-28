package ru.tensor.sbis.design.period_picker.view.content_creator

import android.os.Parcelable
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.container.ContentCreator
import ru.tensor.sbis.design.container.FragmentContent
import ru.tensor.sbis.design.container.SbisContainerImpl
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerHeaderMask
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.view.period_picker.big.PeriodPickerFragment
import java.util.Calendar

/**
 * Реализация [ContentCreator] для контейнера.
 *
 * @author mb.kruglova
 */
@Parcelize
internal class PeriodPickerContainerContentCreator(
    private val startValue: Calendar?,
    private val endValue: Calendar?,
    private val isEnabled: Boolean,
    private val selectionType: SbisPeriodPickerSelectionType,
    private val displayedRange: SbisPeriodPickerRange,
    private val isOneDaySelection: Boolean,
    private val headerMask: SbisPeriodPickerHeaderMask,
    private val isBottomPosition: Boolean,
    private val presetStartValue: Calendar?,
    private val presetEndValue: Calendar?,
    private val mode: SbisPeriodPickerMode,
    private val anchorDate: Calendar?,
    private val requestKey: String,
    private val resultKey: String
) : ContentCreator<FragmentContent>, Parcelable {
    override fun createContent(): FragmentContent {
        return object : FragmentContent {
            override fun getFragment(containerFragment: SbisContainerImpl): Fragment =
                PeriodPickerFragment.create(
                    startValue,
                    endValue,
                    isEnabled,
                    selectionType,
                    displayedRange,
                    headerMask,
                    isBottomPosition = isBottomPosition,
                    presetStartValue,
                    presetEndValue,
                    requestKey,
                    resultKey,
                    mode,
                    isOneDaySelection = isOneDaySelection,
                    anchorDate = anchorDate
                )

            override fun onRestoreFragment(containerFragment: SbisContainerImpl, fragment: Fragment) = Unit

            override fun useDefaultHorizontalOffset(): Boolean = false

            override fun customWidth(): Int = R.dimen.period_picker_width
        }
    }
}