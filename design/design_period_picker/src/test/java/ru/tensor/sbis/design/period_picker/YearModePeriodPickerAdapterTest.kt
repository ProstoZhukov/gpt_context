package ru.tensor.sbis.design.period_picker

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.YearModePeriodPickerAdapter
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.YearModePeriodPickerModel
import ru.tensor.sbis.design.period_picker.view.utils.year
import java.util.Calendar

/**
 * Тестирование адаптера [YearModePeriodPickerAdapter].
 *
 * @author mb.kruglova
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(AndroidJUnit4::class)
class YearModePeriodPickerAdapterTest {

    private val adapter = YearModePeriodPickerAdapter(null)

    private val list = listOf(
        2020,
        YearModePeriodPickerModel(2020, listOf(), listOf(), listOf()),
        2021,
        YearModePeriodPickerModel(2021, listOf(), listOf(), listOf()),
        2022,
        YearModePeriodPickerModel(2022, listOf(), listOf(), listOf())
    )

    @Test
    fun `When adapter is updated then size of item is changed`() {
        assert(adapter.itemCount == 0)

        adapter.update(list)

        assert(adapter.itemCount == list.size)
    }

    @Test
    fun `When adapter is reload while scrolling down then size of item is changed`() {
        adapter.update(list)
        adapter.reload(list, true)

        assert(adapter.itemCount == list.size * 2)
    }

    @Test
    fun `When adapter is reload while scrolling up then size of item is changed`() {
        adapter.update(list)
        adapter.reload(list, false)

        assert(adapter.itemCount == list.size * 2)
    }

    @Test
    fun `When position is equals to 0 then getYearByPosition method returns the first item year`() {
        adapter.update(list)

        val newDate = adapter.getYearByPosition(0)
        assert(newDate == 2020)
    }

    @Test
    fun `When position is more than 0 then getYearByPosition method returns item year`() {
        adapter.update(list)

        val newDate = adapter.getYearByPosition(3)
        assert(newDate == 2021)
    }

    @Test
    fun `When position is less than 0 then getYearByPosition method returns current year`() {
        adapter.update(list)

        val newDate = adapter.getYearByPosition(-1)
        assert(newDate == Calendar.getInstance().year)
    }

    @Test
    fun `When date does not belongs to list then first year position is 1`() {
        adapter.update(list)

        val newDate = adapter.getFirstYearPosition(2019)
        assert(newDate == 1)
    }

    @Test
    fun `When date belongs to list then first year position is found out`() {
        adapter.update(list)

        val newDate = adapter.getYearPosition(2021)
        assert(newDate == 2)
    }

    @Test
    fun `When adapter item size is not empty then last position is more than 0`() {
        adapter.update(list)

        val newDate = adapter.getLastPosition()
        assert(newDate == list.size - 1)
    }
}