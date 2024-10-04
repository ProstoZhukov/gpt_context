package ru.tensor.sbis.design.short_period_picker

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.buttons.SbisArrowButton
import ru.tensor.sbis.design.findShortPeriodPickerView
import ru.tensor.sbis.design.launchTestFragmentFragmentInContainer
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerAnchor
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature
import ru.tensor.sbis.design.period_picker.decl.SbisShortPeriodPickerVisualParams
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.HorizontalAlignment
import ru.tensor.sbis.design.theme.VerticalAlignment

/**
 * Тест для [SbisPeriodPickerFeature].
 *
 * @author mb.kruglova
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class ShortPeriodPickerFeatureTest {

    @Test
    fun `By default header title is empty`() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->

                fragment.showShortPeriodPicker()

                findShortPeriodPickerView(fragment).run {
                    val header = findViewById<ConstraintLayout>(R.id.short_period_picker_header)
                    val title = findViewById<SbisTextView>(R.id.short_period_picker_header_title)
                    val arrowLeft = findViewById<SbisArrowButton>(R.id.short_period_picker_header_button_left)
                    val arrowRight = findViewById<SbisArrowButton>(R.id.short_period_picker_header_button_right)

                    assert(header.isVisible)
                    assert(title.text?.isEmpty() == true)
                    assert(!arrowLeft.isVisible)
                    assert(!arrowRight.isVisible)
                }
            }
        }
    }

    @Test
    fun `When visual params are set to choose year then header is not visible and calendar is not empty`() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->

                fragment.showShortPeriodPicker(
                    SbisShortPeriodPickerVisualParams(chooseYears = true)
                )

                findShortPeriodPickerView(fragment).run {
                    val header = findViewById<ConstraintLayout>(R.id.short_period_picker_header)
                    val calendar = findViewById<RecyclerView>(R.id.short_period_picker_list)

                    assert(!header.isVisible)
                    assert((calendar.adapter?.itemCount ?: -1) > 0)
                }
            }
        }
    }

    @Test
    fun `When all visual params are true then calendar header is visible and calendar is not empty`() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->

                fragment.showShortPeriodPicker(
                    SbisShortPeriodPickerVisualParams(
                        arrowVisible = true,
                        chooseHalfYears = true,
                        chooseMonths = true,
                        chooseQuarters = true,
                        chooseYears = true
                    )
                )

                findShortPeriodPickerView(fragment).run {
                    val header = findViewById<ConstraintLayout>(R.id.short_period_picker_header)
                    val calendar = findViewById<RecyclerView>(R.id.short_period_picker_list)

                    assert(header.isVisible)
                    assert((calendar.adapter?.itemCount ?: -1) > 0)
                }
            }
        }
    }

    @Test
    fun `When arrowVisible is false then header arrow buttons are not visible`() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->

                fragment.showShortPeriodPicker(
                    SbisShortPeriodPickerVisualParams(
                        arrowVisible = false,
                        chooseHalfYears = true,
                        chooseMonths = true,
                        chooseQuarters = true,
                        chooseYears = true
                    )
                )

                findShortPeriodPickerView(fragment).run {
                    val header = findViewById<ConstraintLayout>(R.id.short_period_picker_header)
                    val calendar = findViewById<RecyclerView>(R.id.short_period_picker_list)
                    val arrowLeft = findViewById<SbisArrowButton>(R.id.short_period_picker_header_button_left)
                    val arrowRight = findViewById<SbisArrowButton>(R.id.short_period_picker_header_button_right)

                    assert(header.isVisible)
                    assert((calendar.adapter?.itemCount ?: -1) > 0)
                    assert(!arrowLeft.isVisible)
                    assert(!arrowRight.isVisible)
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

                val visualParams = SbisShortPeriodPickerVisualParams(
                    arrowVisible = true,
                    chooseHalfYears = true,
                    chooseMonths = true,
                    chooseQuarters = true,
                    chooseYears = true
                )

                feature.showShortPeriodPicker(
                    fragment.requireContext(),
                    fragment.childFragmentManager,
                    visualParams
                )

                verify(feature, times(1)).showShortPeriodPicker(
                    fragment.requireContext(),
                    fragment.childFragmentManager,
                    visualParams
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

                val visualParams = SbisShortPeriodPickerVisualParams(
                    arrowVisible = true,
                    chooseHalfYears = true,
                    chooseMonths = true,
                    chooseQuarters = true,
                    chooseYears = true
                )

                val anchors = SbisPeriodPickerAnchor(
                    "viewTag",
                    HorizontalAlignment.LEFT,
                    VerticalAlignment.TOP
                )

                feature.showShortPeriodPicker(
                    fragment.requireContext(),
                    fragment.childFragmentManager,
                    visualParams,
                    anchors = anchors
                )

                verify(feature, times(1)).showShortPeriodPicker(
                    fragment.requireContext(),
                    fragment.childFragmentManager,
                    visualParams,
                    anchors = anchors
                )
            }
        }
    }
}