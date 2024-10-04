package ru.tensor.sbis.design.compact_period_picker

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import ru.tensor.sbis.design.findCompactPeriodPickerView
import ru.tensor.sbis.design.getMonthLabel
import ru.tensor.sbis.design.launchTestFragmentFragmentInContainer
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerAnchor
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl
import ru.tensor.sbis.design.period_picker.view.utils.month
import ru.tensor.sbis.design.period_picker.view.utils.removeTime
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.HorizontalAlignment
import ru.tensor.sbis.design.theme.VerticalAlignment
import java.util.Calendar

/**
 * Тест для [SbisPeriodPickerFeature].
 *
 * @author mb.kruglova
 */
@RunWith(AndroidJUnit4::class)
class CompactPeriodPickerFeatureTest {

    private val currentDate = Calendar.getInstance().removeTime()

    @Test
    fun `By default month label is month of current day`() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->

                fragment.showCompactPeriodPicker()

                findCompactPeriodPickerView(fragment).run {
                    val monthLabel = findViewById<SbisTextView>(R.id.month_label)

                    assert(monthLabel.text == resources.getMonthLabel(currentDate.month))
                }
            }
        }
    }

    @Test
    fun `When device is a tablet then period picker is shown in container`() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->
                val feature = spy(SbisPeriodPickerFeatureImpl())

                feature.isTablet = true

                feature.showCompactPeriodPicker(fragment.requireContext(), fragment.childFragmentManager)

                verify(feature, times(1)).showCompactPeriodPicker(
                    fragment.requireContext(),
                    fragment.childFragmentManager
                )
            }
        }
    }

    @Test
    fun `When device is a tablet and anchors are set then period picker is shown by anchors`() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->
                val feature = spy(SbisPeriodPickerFeatureImpl())

                feature.isTablet = true
                val anchors = SbisPeriodPickerAnchor(
                    "viewTag",
                    HorizontalAlignment.LEFT,
                    VerticalAlignment.TOP
                )

                feature.showCompactPeriodPicker(
                    fragment.requireContext(),
                    fragment.childFragmentManager,
                    anchors = anchors
                )

                verify(feature, times(1)).showCompactPeriodPicker(
                    fragment.requireContext(),
                    fragment.childFragmentManager,
                    anchors = anchors
                )
            }
        }
    }
}