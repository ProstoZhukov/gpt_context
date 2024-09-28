package ru.tensor.sbis.design.period_picker

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.junit.runner.RunWith
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCustomTheme
import ru.tensor.sbis.design.period_picker.view.models.MarkerType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumSelection
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.MonthModePeriodPickerAdapter
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.model.DayItemModel
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.model.DayModel
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.model.EmptyModel
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.model.MonthLabelModel
import ru.tensor.sbis.design.period_picker.view.utils.MIN_DATE
import ru.tensor.sbis.design.period_picker.view.utils.dayOfMonth
import ru.tensor.sbis.design.period_picker.view.utils.dayOfWeek
import ru.tensor.sbis.design.period_picker.view.utils.month
import ru.tensor.sbis.design.period_picker.view.utils.removeTime
import ru.tensor.sbis.design.period_picker.view.utils.year
import java.util.Calendar
import java.util.GregorianCalendar

/**
 * Тестирование адаптера [MonthModePeriodPickerAdapter].
 *
 * @author mb.kruglova
 */
@RunWith(AndroidJUnit4::class)
class MonthModePeriodPickerAdapterTest {

    private val adapter = MonthModePeriodPickerAdapter(null)
    private val decDate = GregorianCalendar(2023, 11, 15).removeTime()
    private val janDate = GregorianCalendar(2024, 0, 15).removeTime()
    private val febDate = GregorianCalendar(2024, 1, 15).removeTime()
    private val marDate = GregorianCalendar(2024, 2, 15).removeTime()

    @Test
    fun `When adapter is updated then size of item is changed`() {
        assert(adapter.itemCount == 0)

        val list = getList()
        adapter.update(list)

        assert(adapter.itemCount == list.size)
    }

    @Test
    fun `When adapter is reload while scrolling down then size of item is changed`() {
        val list = getList()
        adapter.update(list)
        adapter.reload(list, true)

        assert(adapter.itemCount == list.size * 2)
    }

    @Test
    fun `When adapter is reload while scrolling up then size of item is changed`() {
        val list = getList()
        adapter.update(list)
        adapter.reload(list, false)

        assert(adapter.itemCount == list.size * 2)
    }

    @Test
    @DisplayName(
        "When position is more than or equals to 0 " +
            "then getDateByPosition method returns date of adapter item by position"
    )
    fun getDateByPosition() {
        val list = getList()
        adapter.update(list)

        val newDate = adapter.getDateByPosition(0)
        assert(newDate.timeInMillis == janDate.timeInMillis)
    }

    @Test
    fun `When position is less than 0 then getDateByPosition method returns MIN_DATE`() {
        val list = getList()
        adapter.update(list)

        val newDate = adapter.getDateByPosition(-1)
        assert(newDate.timeInMillis == MIN_DATE.timeInMillis)
    }

    @Test
    @DisplayName(
        "When position is more than or equals to 0 " +
            "then getHeaderDateByPosition method returns header date by position"
    )
    fun getHeaderDateByPosition() {
        val list = getList()
        adapter.update(list)

        val newDate = adapter.getHeaderDateByPosition(0)
        assert(newDate.timeInMillis == janDate.timeInMillis)
    }

    @Test
    @DisplayName(
        "When position is less than 0 " +
            "then getHeaderDateByPosition method returns MIN_DATE"
    )
    fun getZeroHeaderDateByPosition() {
        val list = getList()
        adapter.update(list)

        val newDate = adapter.getHeaderDateByPosition(-1)
        assert(newDate.timeInMillis == MIN_DATE.timeInMillis)
    }

    @Test
    fun `When position is equals to 0 then getMonthByPosition method returns month of item by position`() {
        val list = getList()
        adapter.update(list)

        val newDate = adapter.getMonthByPosition(0, Calendar.DECEMBER)
        assert(newDate == janDate.month)
    }

    @Test
    @DisplayName(
        "When position is more than 0 and current month is january " +
            "then getMonthByPosition method returns -1 by position"
    )
    fun getMonthByNoZeroPosition() {
        val list = getListWithThreeMonths()
        adapter.update(list)

        val newDate = adapter.getMonthByPosition(3, Calendar.DECEMBER)
        assert(newDate == -1)
    }

    @Test
    @DisplayName(
        "When position is more than 0 and current month is not january " +
            "then getMonthByPosition method returns month of item by position"
    )
    fun getMonthByPosition() {
        val list = getList()
        adapter.update(list)

        val newDate = adapter.getMonthByPosition(3, Calendar.DECEMBER)
        assert(newDate == janDate.month)
    }

    @Test
    fun `When position is less than 0 then getMonthByPosition method returns default month`() {
        val list = getList()
        adapter.update(list)

        val newDate = adapter.getMonthByPosition(-1, Calendar.DECEMBER)
        assert(newDate == Calendar.DECEMBER)
    }

    @Test
    fun `When position is more than or equals to 0 then getMonthByPosition method returns year of item by position`() {
        val list = getList()
        adapter.update(list)

        val newDate = adapter.getYearByPosition(0)
        assert(newDate == janDate.year)
    }

    @Test
    fun `When position is less than 0 then getMonthByPosition method returns MIN_DATE year`() {
        val list = getList()
        adapter.update(list)

        val newDate = adapter.getYearByPosition(-1)
        assert(newDate == MIN_DATE.year)
    }

    @Test
    fun `When date belongs to list then first month position is found out`() {
        val list = getList()
        adapter.update(list)

        val position = adapter.getFirstMonthPosition(Calendar.FEBRUARY, 2024, false)
        assert(position == 3)
    }

    @Test
    fun `When date does not belong to list then first month position is 0`() {
        val list = getList()
        adapter.update(list)

        val position = adapter.getFirstMonthPosition(Calendar.DECEMBER, 2024, false)
        assert(position == 0)
    }

    @Test
    fun `When date belongs to list then item position is found out`() {
        val list = getList()
        adapter.update(list)

        val position = adapter.getPosition(janDate)
        assert(position == 2)
    }

    @Test
    fun `When date does not belong to list then item position is 0`() {
        val list = getList()
        adapter.update(list)

        val position = adapter.getPosition(marDate)
        assert(position == 0)
    }

    @Test
    fun `When adapter item size is not empty then last position is more than 0`() {
        val list = getList()
        adapter.update(list)

        val position = adapter.getLastPosition()
        assert(position == list.size - 1)
    }

    private fun getMonth(date: Calendar) = listOf(
        MonthLabelModel(date, "", true),
        EmptyModel(date),
        DayModel(
            date.dayOfMonth,
            date.dayOfWeek,
            date,
            QuantumSelection(),
            counter = "",
            MarkerType.NO_MARKER,
            isCurrent = false,
            isRangePart = true,
            customTheme = SbisPeriodPickerDayCustomTheme()
        )
    )

    private fun getList(): List<DayItemModel> {
        val list = mutableListOf<DayItemModel>()
        list.addAll(getMonth(janDate))
        list.addAll(getMonth(febDate))
        return list
    }

    private fun getListWithThreeMonths(): List<DayItemModel> {
        val list = mutableListOf<DayItemModel>()
        list.addAll(getMonth(decDate))
        list.addAll(getMonth(janDate))
        list.addAll(getMonth(febDate))
        return list
    }
}