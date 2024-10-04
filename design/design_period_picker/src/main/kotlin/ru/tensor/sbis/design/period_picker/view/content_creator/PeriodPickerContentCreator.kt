package ru.tensor.sbis.design.period_picker.view.content_creator

import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerHeaderMask
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.view.period_picker.big.PeriodPickerFragment
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import java.util.Calendar

/**
 * Реализация [ContentCreatorParcelable] для шторки.
 *
 * @author mb.kruglova
 */
@Parcelize
internal class PeriodPickerContentCreator(
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
) : ContentCreatorParcelable {

    override fun createFragment(): Fragment = PeriodPickerFragment.create(
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
        anchorDate
    )
}