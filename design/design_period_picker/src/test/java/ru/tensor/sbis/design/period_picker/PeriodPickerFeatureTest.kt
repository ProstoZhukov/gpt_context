package ru.tensor.sbis.design.period_picker

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.junit.runner.RunWith
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.SbisToggleRoundButton
import ru.tensor.sbis.design.findPeriodPickerView
import ru.tensor.sbis.design.launchTestFragmentFragmentInContainer
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerAnchor
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerHeaderMask
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl
import ru.tensor.sbis.design.period_picker.view.utils.removeTime
import ru.tensor.sbis.design.theme.HorizontalAlignment
import ru.tensor.sbis.design.theme.VerticalAlignment
import ru.tensor.sbis.design.view.input.base.ValidationStatus
import ru.tensor.sbis.design.view.input.mask.date.DateInputView
import java.util.Calendar
import java.util.GregorianCalendar

/**
 * Тест для [SbisPeriodPickerFeature].
 *
 * @author mb.kruglova
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class PeriodPickerFeatureTest {

    private val startValue = "101223"
    private val endValue = "121224"
    private val currentDate = Calendar.getInstance().removeTime()

    @Test
    fun `By default period is not selected and year mode is displayed`() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->

                fragment.showPeriodPicker()

                findPeriodPickerView(fragment).run {
                    val startDate = findViewById<DateInputView>(R.id.start_date)
                    val endDate = findViewById<DateInputView>(R.id.end_date)
                    val mode = findViewById<SbisToggleRoundButton>(R.id.mode_button)

                    assertNull(startDate.getDate())
                    assertNull(endDate.getDate())
                    assert(!mode.isChecked)
                }
            }
        }
    }

    @Test
    @DisplayName(
        "When period is not selected and complete button are clicked" +
            " then date input view validation status is error"
    )
    fun showPeriodPickerWithUnselectedPeriod() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->

                fragment.showPeriodPicker()

                findPeriodPickerView(fragment).run {
                    val startDate = findViewById<DateInputView>(R.id.start_date)
                    val endDate = findViewById<DateInputView>(R.id.end_date)
                    val complete = findViewById<SbisRoundButton>(R.id.complete_button)

                    complete.performClick()

                    assert(startDate.validationStatus is ValidationStatus.Error)
                    assert(endDate.validationStatus is ValidationStatus.Error)
                }
            }
        }
    }

    @Test
    @DisplayName(
        "When start date of period is selected and complete button are clicked" +
            " then date input view validation status is error"
    )
    fun showPeriodPickerWithUnselectedEndDate() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->

                fragment.showPeriodPicker()

                findPeriodPickerView(fragment).run {
                    val startDate = findViewById<DateInputView>(R.id.start_date)
                    val endDate = findViewById<DateInputView>(R.id.end_date)
                    val complete = findViewById<SbisRoundButton>(R.id.complete_button)

                    startDate.value = startValue
                    complete.performClick()

                    assert(startDate.validationStatus is ValidationStatus.Error)
                    assert(endDate.validationStatus is ValidationStatus.Error)
                }
            }
        }
    }

    @Test
    @DisplayName(
        "When end date of period is selected and complete button are clicked" +
            " then date input view validation status is error"
    )
    fun showPeriodPickerWithUnselectedStartDate() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->

                fragment.showPeriodPicker()

                findPeriodPickerView(fragment).run {
                    val startDate = findViewById<DateInputView>(R.id.start_date)
                    val endDate = findViewById<DateInputView>(R.id.end_date)
                    val complete = findViewById<SbisRoundButton>(R.id.complete_button)

                    endDate.value = endValue
                    complete.performClick()

                    assert(startDate.validationStatus is ValidationStatus.Error)
                    assert(endDate.validationStatus is ValidationStatus.Error)
                }
            }
        }
    }

    @Test
    fun `When period is selected and complete button are clicked then date input view validation status is default`() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->

                fragment.showPeriodPicker()

                findPeriodPickerView(fragment).run {
                    val startDate = findViewById<DateInputView>(R.id.start_date)
                    val endDate = findViewById<DateInputView>(R.id.end_date)
                    val complete = findViewById<SbisRoundButton>(R.id.complete_button)

                    startDate.value = startValue
                    endDate.value = endValue
                    complete.performClick()

                    assert(startDate.validationStatus is ValidationStatus.Default)
                    assert(startDate.value == "10.12.23")
                    assert(endDate.validationStatus is ValidationStatus.Default)
                    assert(endDate.value == "12.12.24")
                }
            }
        }
    }

    @Test
    fun `When period is preselected then date input view is filled`() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->

                fragment.showPeriodPicker(currentDate, currentDate)

                findPeriodPickerView(fragment).run {
                    val startDate = findViewById<DateInputView>(R.id.start_date)
                    val endDate = findViewById<DateInputView>(R.id.end_date)

                    assert(startDate.getDate()?.timeInMillis == currentDate.timeInMillis)
                    assert(endDate.getDate()?.timeInMillis == currentDate.timeInMillis)
                }
            }
        }
    }

    @Test
    fun `When period is preselected and it is less than month then month mode is displayed`() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->

                val date = Calendar.getInstance().removeTime()
                fragment.showPeriodPicker(date, date)

                findPeriodPickerView(fragment).run {
                    val mode = findViewById<SbisToggleRoundButton>(R.id.mode_button)

                    assert(mode.isChecked)
                }
            }
        }
    }

    @Test
    fun `When period is preselected and it equals to month then year mode is displayed`() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->

                val dateStart = GregorianCalendar(2024, 1, 1).removeTime()
                val endStart = GregorianCalendar(2024, 1, 29).removeTime()
                fragment.showPeriodPicker(dateStart, endStart)

                findPeriodPickerView(fragment).run {
                    val mode = findViewById<SbisToggleRoundButton>(R.id.mode_button)

                    assert(!mode.isChecked)
                }
            }
        }
    }

    @Test
    @DisplayName(
        "When period is preselected, it is more than month and it is not a multiple of quantum" +
            " then month mode is displayed"
    )
    fun showPeriodPickerWithPreset() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->

                val dateStart = GregorianCalendar(2024, 0, 1).removeTime()
                val endStart = GregorianCalendar(2024, 1, 25).removeTime()
                fragment.showPeriodPicker(dateStart, endStart)

                findPeriodPickerView(fragment).run {
                    val mode = findViewById<SbisToggleRoundButton>(R.id.mode_button)

                    assert(mode.isChecked)
                }
            }
        }
    }

    @Test
    fun `When period is preselected, it is more than month and it is a multiple of quantum then year mode is displayed`() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->

                val dateStart = GregorianCalendar(2024, 0, 1).removeTime()
                val endStart = GregorianCalendar(2024, 2, 31).removeTime()
                fragment.showPeriodPicker(dateStart, endStart)

                findPeriodPickerView(fragment).run {
                    val mode = findViewById<SbisToggleRoundButton>(R.id.mode_button)

                    assert(!mode.isChecked)
                }
            }
        }
    }

    @Test
    fun `When period is preselected and header mask is FULL_YEAR then date input view has full year`() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->

                val dateStart = GregorianCalendar(2024, 1, 1).removeTime()
                val endStart = GregorianCalendar(2024, 1, 29).removeTime()

                fragment.showPeriodPicker(dateStart, endStart, SbisPeriodPickerHeaderMask.FULL_YEAR)

                findPeriodPickerView(fragment).run {
                    val startDate = findViewById<DateInputView>(R.id.start_date)
                    val endDate = findViewById<DateInputView>(R.id.end_date)

                    assert(startDate.value == "01.02.2024")
                    assert(endDate.value == "29.02.2024")
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

                feature.showPeriodPicker(fragment.requireContext(), fragment.childFragmentManager)

                verify(feature, times(1)).showPeriodPicker(fragment.requireContext(), fragment.childFragmentManager)
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

                feature.showPeriodPicker(fragment.requireContext(), fragment.childFragmentManager, anchors = anchors)

                verify(feature, times(1)).showPeriodPicker(
                    fragment.requireContext(),
                    fragment.childFragmentManager,
                    anchors = anchors
                )
            }
        }
    }
}