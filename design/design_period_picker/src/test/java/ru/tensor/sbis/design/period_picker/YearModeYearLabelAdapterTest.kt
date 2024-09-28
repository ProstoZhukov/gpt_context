package ru.tensor.sbis.design.period_picker

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumSelection
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.listeners.YearLabelListener
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel.YearLabelModel
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.header.YearLabelAdapter

/**
 * Тестирование адаптера [YearLabelAdapter].
 *
 * @author mb.kruglova
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(AndroidJUnit4::class)
class YearModeYearLabelAdapterTest {

    private val listener: YearLabelListener = mock()
    private val adapter = YearLabelAdapter(listener)

    private val list = listOf(
        YearLabelModel("2020", 2020, QuantumSelection(), true),
        YearLabelModel("2021", 2021, QuantumSelection(), true),
        YearLabelModel("2022", 2022, QuantumSelection(), true),
        YearLabelModel("2023", 2023, QuantumSelection(), true),
        YearLabelModel("2024", 2024, QuantumSelection(), true)
    )

    @Test
    fun `When adapter is updated then size of item is changed`() {
        assert(adapter.itemCount == 0)

        adapter.update(list)

        assert(adapter.itemCount == list.size)
    }

    @Test
    fun `Method getYearPositionWithShift returns position of year with shift in 3 years`() {
        adapter.update(list)
        val pos = adapter.getYearPositionWithShift(2024)

        assert(pos == 1)
    }
}