package ru.tensor.sbis.design.period_picker

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.YearModePeriodPickerView
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.listeners.YearLabelListener
import ru.tensor.sbis.design.period_picker.view.utils.month
import ru.tensor.sbis.design.period_picker.view.utils.removeTime
import ru.tensor.sbis.design.period_picker.view.utils.year
import java.util.GregorianCalendar

/**
 * Тестирование слушателя [YearLabelListener].
 *
 * @author mb.kruglova
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class YearModeYearLabelListenerTest {

    private val dispatch: (YearModePeriodPickerView.Event) -> Unit = mock()

    private val listener = spy(YearLabelListener(dispatch))

    private val date = GregorianCalendar(2024, 0, 15).removeTime()

    @Test
    fun `When onClickYearLabel method is called then dispatch invokes`() {
        listener.onClickYearLabel(date.year, date.month)

        verify(dispatch).invoke(any())
    }

    @Test
    fun `When onUpdateYearLabel method is called then dispatch invokes`() {
        listener.onUpdateYearLabel(date.year)

        verify(dispatch).invoke(any())
    }

    @Test
    fun `When onReloadCalendar method is called for the next page then dispatch invokes`() {
        listener.onReloadCalendar(true, date.year)

        verify(dispatch).invoke(any())
    }

    @Test
    fun `When onReloadCalendar method is called for the previous page then dispatch invokes`() {
        listener.onReloadCalendar(false, date.year)

        verify(dispatch).invoke(any())
    }
}