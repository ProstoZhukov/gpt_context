package ru.tensor.sbis.design.period_picker

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.annotation.Config
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.drawables.models.QuantumSelection
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.QuantumAdapter
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.delegates.QuantumDelegate
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.model.QuantumItemModel
import java.util.Calendar

/**
 * Тестирование адаптера [QuantumAdapter].
 *
 * @author mb.kruglova
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(AndroidJUnit4::class)
class YearModeQuantumAdapterTest {

    private val delegate: QuantumDelegate<*, *> = mock()

    private val adapter = QuantumAdapter(delegate)

    @Test
    fun `When adapter is reload then size of item is changed`() {
        assert(adapter.itemCount == 0)

        val list = listOf(
            QuantumItemModel.YearLabelModel("2024", 2024, QuantumSelection(), true),
            QuantumItemModel.HalfYearModel("I half year", 2024, Calendar.JANUARY, QuantumSelection(), true),
            QuantumItemModel.QuarterModel("I quarter", 2024, Calendar.JANUARY, QuantumSelection(), true),
            QuantumItemModel.MonthModel("january", 2024, Calendar.JANUARY, QuantumSelection(), true)
        )
        adapter.reload(list)

        assert(adapter.itemCount == list.size)
    }
}