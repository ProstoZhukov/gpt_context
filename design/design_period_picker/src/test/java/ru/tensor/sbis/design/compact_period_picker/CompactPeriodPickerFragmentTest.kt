package ru.tensor.sbis.design.compact_period_picker

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.design.findCompactPeriodPickerFragmentView
import ru.tensor.sbis.design.getMonthLabel
import ru.tensor.sbis.design.launchTestFragmentFragmentInContainer
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.decl.SbisCompactPeriodPickerFragmentFeature
import ru.tensor.sbis.design.period_picker.view.utils.month
import ru.tensor.sbis.design.period_picker.view.utils.removeTime
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import java.util.Calendar

/**
 * Тест для [SbisCompactPeriodPickerFragmentFeature].
 *
 * @author mb.kruglova
 */
@RunWith(AndroidJUnit4::class)
class CompactPeriodPickerFragmentTest {

    private val currentDate = Calendar.getInstance().removeTime()

    @Test
    fun `By default month label is month of current day`() {
        with(launchTestFragmentFragmentInContainer()) {
            onFragment { fragment ->

                fragment.showCompactPeriodPickerFragment()

                findCompactPeriodPickerFragmentView(fragment).run {
                    val monthLabel = findViewById<SbisTextView>(R.id.month_label)

                    assert(monthLabel.text == resources.getMonthLabel(currentDate.month))
                }
            }
        }
    }
}