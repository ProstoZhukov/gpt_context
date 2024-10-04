package ru.tensor.sbis.design

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerAnchor
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCustomTheme
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayType
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerHeaderMask
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.decl.SbisShortPeriodPickerVisualParams
import ru.tensor.sbis.design.period_picker.decl.createSbisCompactPeriodPickerFragmentFeature
import ru.tensor.sbis.design.period_picker.decl.createSbisPeriodPickerFeature
import java.util.Calendar

/**
 * Тестовый фрагмент компонента Выбор периода.
 *
 * @author mb.kruglova
 */
class PeriodPickerTestFragment : Fragment() {

    private val periodPickerFeature by createSbisPeriodPickerFeature()
    private val periodPickerFragmentFeature by createSbisCompactPeriodPickerFragmentFeature()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return View(requireContext())
    }

    /** Показать Большой выбор периода. */
    fun showPeriodPicker(
        startValue: Calendar? = null,
        endValue: Calendar? = null,
        headerMask: SbisPeriodPickerHeaderMask = SbisPeriodPickerHeaderMask.DEFAULT,
        anchors: SbisPeriodPickerAnchor? = null
    ) {
        periodPickerFeature.showPeriodPicker(
            this.requireContext(),
            this.childFragmentManager,
            startValue,
            endValue,
            headerMask = headerMask,
            anchors = anchors
        )
    }

    /** Показать Быстрый выбор периода. */
    fun showShortPeriodPicker(
        visualParams: SbisShortPeriodPickerVisualParams = SbisShortPeriodPickerVisualParams(),
        startValue: Calendar? = null,
        endValue: Calendar? = null,
        anchors: SbisPeriodPickerAnchor? = null
    ) {
        periodPickerFeature.showShortPeriodPicker(
            this.requireContext(),
            this.childFragmentManager,
            visualParams,
            startValue = startValue,
            endValue = endValue,
            anchors = anchors
        )
    }

    /** Показать Компактный выбор периода. */
    fun showCompactPeriodPicker(
        anchors: SbisPeriodPickerAnchor? = null
    ) {
        periodPickerFeature.showCompactPeriodPicker(
            this.requireContext(),
            this.childFragmentManager,
            anchors = anchors
        )
    }

    /** Показать фрагмент Компактный выбор периода. */
    fun showCompactPeriodPickerFragment() {
        periodPickerFragmentFeature.showCompactPeriodPicker(
            this,
            0,
            null,
            null,
            true,
            SbisPeriodPickerSelectionType.Single,
            SbisPeriodPickerDayType.Simple,
            null,
            null,
            { SbisPeriodPickerDayCustomTheme() },
            false,
            null,
            "requestKey",
            "resultKey"
        )
    }
}