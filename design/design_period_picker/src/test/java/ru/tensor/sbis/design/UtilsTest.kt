package ru.tensor.sbis.design

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.view.models.SelectionType
import ru.tensor.sbis.design.period_picker.view.utils.checkAndResetPreviousSelection
import ru.tensor.sbis.design.period_picker.view.utils.getCalendarMode
import ru.tensor.sbis.design.period_picker.view.utils.getFirstDisplayedRange
import ru.tensor.sbis.design.period_picker.view.utils.getFirstVisibleItemPosition
import ru.tensor.sbis.design.period_picker.view.utils.getNewPeriod
import ru.tensor.sbis.design.period_picker.view.utils.getParamsFromArgs
import ru.tensor.sbis.design.period_picker.view.utils.getPeriod
import ru.tensor.sbis.design.period_picker.view.utils.getPresetPeriod
import ru.tensor.sbis.design.period_picker.view.utils.isClosePeriodPicker
import ru.tensor.sbis.design.period_picker.view.utils.removeTime
import ru.tensor.sbis.design.period_picker.view.utils.setSbisTextViewColor
import ru.tensor.sbis.design.period_picker.view.utils.updateDate
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.TextColor
import org.junit.jupiter.api.DisplayName
import ru.tensor.sbis.design.view.input.mask.date.DateInputView
import java.util.Calendar
import java.util.GregorianCalendar

/**
 * Тестирование методов вспомогательного класса [Utils].
 *
 * @author mb.kruglova
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class UtilsTest {

    private val mockContext: Context = mock()
    private val key = "key"
    private val position = 1

    private val mockBundle = mock<Bundle> {
        on { getParcelable(key) as? SbisPeriodPickerSelectionType }.thenReturn(SbisPeriodPickerSelectionType.Single)
    }

    private val textColor = mock<TextColor> {
        on { getValue(mockContext) }.thenReturn(Color.MAGENTA)
    }

    private val startMonth = GregorianCalendar(2024, 0, 1).removeTime()
    private val endMonth = GregorianCalendar(2024, 0, 31).removeTime()

    private val startYear = GregorianCalendar(2025, 0, 1).removeTime()
    private val endYear = GregorianCalendar(2025, 11, 31).removeTime()

    private val date = GregorianCalendar(2024, 0, 15).removeTime()

    private val layoutManager: LinearLayoutManager = mock {
        on { findFirstVisibleItemPosition() }.thenReturn(position)
    }

    private val adapter: RecyclerView.Adapter<*> = mock {
        on { itemCount }.thenReturn(10)
    }

    @Test
    fun `When param is parcelable then getParamsFromArgs method returns its value`() {
        val selectionType = getParamsFromArgs(mockBundle, key, SbisPeriodPickerSelectionType::class.java)

        assert(selectionType is SbisPeriodPickerSelectionType.Single)
    }

    @Test
    @DisplayName(
        "Method getFirstVisibleItemPosition returns first visible item position " +
            "if it is not the same as current one"
    )
    fun getFirstVisibleItemPosition() {
        val pos = getFirstVisibleItemPosition(layoutManager, adapter, 5)

        assert(pos == position)
    }

    @Test
    fun `Method getFirstDisplayedRange returns first range from list`() {
        val range1 = SbisPeriodPickerRange(startMonth, endMonth)
        val range2 = SbisPeriodPickerRange(startYear, endYear)
        val range = getFirstDisplayedRange(listOf(range1, range2))

        assert(range == range1)
    }

    @Test
    fun `When displayed range is null then getFirstDisplayedRange method returns default range`() {
        val defRange = SbisPeriodPickerRange()
        val range = getFirstDisplayedRange(null)

        assert(range == defRange)
    }

    @Test
    fun `Method setSbisTextViewColor sets new color to SbisTextView`() {
        val view: SbisTextView = mock()
        view.setSbisTextViewColor(textColor, mockContext)
        val arg = ArgumentCaptor.forClass(Int::class.java)
        verify(view, atLeastOnce()).setTextColor(arg.capture())
    }

    @Test
    fun `When new date is null then updateDate method reset value of DateInputView`() {
        val view: DateInputView = mock()
        view.updateDate(null)
        val arg = ArgumentCaptor.forClass(CharSequence::class.java)
        verify(view, atLeastOnce()).value = arg.capture()
    }

    @Test
    fun `When new date is not null then updateDate method updates value of DateInputView`() {
        val view: DateInputView = mock()
        view.updateDate(startMonth)
        val arg = ArgumentCaptor.forClass(Calendar::class.java)
        verify(view, atLeastOnce()).updateValue(arg.capture())
    }

    @Test
    fun `Method getPresetPeriod returns pair of dates`() {
        val (start, end) = getPresetPeriod(date, date)

        assert(start.timeInMillis == date.timeInMillis)
        assert(end?.timeInMillis == date.timeInMillis)
    }

    @Test
    fun `When one date of two is null then end date of period is null`() {
        val (start, end) = getPresetPeriod(null, date)

        assert(start.timeInMillis == date.timeInMillis)
        assertNull(end)
    }

    @Test
    fun `Method getNewPeriod returns pair of dates`() {
        val (start, end) = getNewPeriod(date, date)

        assert(start.timeInMillis == date.timeInMillis)
        assert(end?.timeInMillis == date.timeInMillis)
    }

    @Test
    fun `When end date is null then getNewPeriod method returns pair of dates where end date is null`() {
        val (start, end) = getNewPeriod(date, null)

        assert(start.timeInMillis == date.timeInMillis)
        assertNull(end)
    }

    @Test
    fun `When end date is less than start date then getNewPeriod method returns pair of reversed dates`() {
        val newDate = GregorianCalendar(2024, 0, 16).removeTime()
        val (start, end) = getNewPeriod(newDate, date)

        assert(start.timeInMillis == date.timeInMillis)
        assert(end?.timeInMillis == newDate.timeInMillis)
    }

    @Test
    fun `When the next and the previous selections are not the same then the previous selection is reset`() {
        val isReset = checkAndResetPreviousSelection(
            SelectionType.NO_SELECTION,
            SelectionType.COMPLETE_SELECTION,
            date,
            date,
            mock()
        )

        assert(isReset)
    }

    @Test
    fun `When start and end dates are not null then the previous selection is reset`() {
        val isReset = checkAndResetPreviousSelection(
            SelectionType.NO_SELECTION,
            SelectionType.NO_SELECTION,
            date,
            date,
            mock()
        )

        assert(isReset)
    }

    @Test
    @DisplayName(
        "When the next and the previous selections are the same and start and end dates are null " +
            "then the previous selection is not reset"
    )
    fun resetSelection() {
        val isReset = checkAndResetPreviousSelection(
            SelectionType.NO_SELECTION,
            SelectionType.NO_SELECTION,
            null,
            null,
            mock()
        )

        assert(!isReset)
    }

    @Test
    @DisplayName(
        "When the next and the previous selections are not the same and end date is handled" +
            " then the previous selection is reset"
    )
    fun resetSelectionWithHandleEndPeriod() {
        val isReset = checkAndResetPreviousSelection(
            SelectionType.NO_SELECTION,
            SelectionType.COMPLETE_SELECTION,
            date,
            null,
            { date },
            mock()
        )

        assert(isReset)
    }

    @Test
    @DisplayName(
        "When the next and the previous selections are not the same and end date is handled and start date is null" +
            " then the previous selection is not reset"
    )
    fun resetSelectionWithHandleEndPeriodForNullStartDate() {
        val isReset = checkAndResetPreviousSelection(
            SelectionType.NO_SELECTION,
            SelectionType.COMPLETE_SELECTION,
            null,
            date,
            { date },
            mock()
        )

        assert(!isReset)
    }

    @Test
    fun `When click is single then period picker is closed`() {
        var isClosed = isClosePeriodPicker(true, null)

        assert(isClosed)

        isClosed = isClosePeriodPicker(true, date)

        assert(isClosed)
    }

    @Test
    fun `When click is not single and end date is null then period picker is not closed`() {
        val isClosed = isClosePeriodPicker(false, null)

        assert(!isClosed)
    }

    @Test
    fun `When click is not single and end date is not null then period picker is closed`() {
        val isClosed = isClosePeriodPicker(false, date)

        assert(isClosed)
    }

    @Test
    fun `Method getPeriod returns SbisPeriodPickerRange`() {
        var range = getPeriod(date, date)
        assert(range.startDate?.timeInMillis == date.timeInMillis)
        assert(range.endDate?.timeInMillis == date.timeInMillis)

        range = getPeriod(null, null)
        assertNull(range.startDate)
        assertNull(range.endDate)

        val newDate = GregorianCalendar(2024, 0, 16).removeTime()
        range = getPeriod(newDate, date)
        assert(range.startDate?.timeInMillis == date.timeInMillis)
        assert(range.endDate?.timeInMillis == newDate.timeInMillis)
    }

    @Test
    fun `When period is less than month and default mode is YEAR then mode is MONTH`() {
        val mode = getCalendarMode(startMonth, date, false, SbisPeriodPickerMode.YEAR)
        assert(mode == SbisPeriodPickerMode.MONTH)
    }

    @Test
    fun `When period is month and default mode is MONTH then mode is YEAR`() {
        val mode = getCalendarMode(startMonth, endMonth, false, SbisPeriodPickerMode.MONTH)
        assert(mode == SbisPeriodPickerMode.YEAR)
    }

    @Test
    fun `When start and end dates are null, default mode is YEAR and just one day is selected then mode is MONTH`() {
        val mode = getCalendarMode(null, null, true, SbisPeriodPickerMode.YEAR)
        assert(mode == SbisPeriodPickerMode.MONTH)
    }

    @Test
    fun `When period is year, default mode is YEAR and just one day is selected then mode is MONTH`() {
        val mode = getCalendarMode(startYear, endYear, true, SbisPeriodPickerMode.YEAR)
        assert(mode == SbisPeriodPickerMode.MONTH)
    }

    @Test
    fun `When period is year and default mode is MONTH then mode is YEAR`() {
        val mode = getCalendarMode(startYear, endYear, false, SbisPeriodPickerMode.MONTH)
        assert(mode == SbisPeriodPickerMode.YEAR)
    }
}