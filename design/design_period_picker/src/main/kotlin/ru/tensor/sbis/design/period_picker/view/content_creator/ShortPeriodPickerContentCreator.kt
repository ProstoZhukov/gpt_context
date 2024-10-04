package ru.tensor.sbis.design.period_picker.view.content_creator

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisShortPeriodPickerVisualParams
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_ANCHOR_DATE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_DISPLAYED_RANGE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_END_VALUE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_IS_BOTTOM_POSITION
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_IS_ENABLED
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_REQUEST_KEY
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_RESULT_KEY
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_START_VALUE
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl.Companion.ARG_VISUAL_PARAMS
import ru.tensor.sbis.design.period_picker.view.short_period_picker.ShortPeriodPickerFragment
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import java.util.Calendar

/**
 * Реализация [ContentCreatorParcelable] для шторки.
 *
 * @author mb.kruglova
 */
@Parcelize
internal class ShortPeriodPickerContentCreator(
    private val visualParams: SbisShortPeriodPickerVisualParams,
    private val displayedRange: SbisPeriodPickerRange,
    private val isBottomPosition: Boolean,
    private val startValue: Calendar?,
    private val endValue: Calendar?,
    private val isEnabled: Boolean,
    private val anchorDate: Calendar?,
    private val requestKey: String,
    private val resultKey: String
) : ContentCreatorParcelable {

    override fun createFragment(): Fragment = ShortPeriodPickerFragment().apply {
        arguments = bundleOf(
            ARG_VISUAL_PARAMS to visualParams,
            ARG_DISPLAYED_RANGE to displayedRange,
            ARG_IS_BOTTOM_POSITION to isBottomPosition,
            ARG_START_VALUE to startValue?.timeInMillis,
            ARG_END_VALUE to endValue?.timeInMillis,
            ARG_IS_ENABLED to isEnabled,
            ARG_ANCHOR_DATE to anchorDate?.timeInMillis,
            ARG_REQUEST_KEY to requestKey,
            ARG_RESULT_KEY to resultKey
        )
    }
}