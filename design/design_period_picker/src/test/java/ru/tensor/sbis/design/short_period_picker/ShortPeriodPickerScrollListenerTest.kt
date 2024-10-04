package ru.tensor.sbis.design.short_period_picker

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.ShortPeriodPickerItem
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.ShortPeriodPickerListAdapter
import ru.tensor.sbis.design.period_picker.view.short_period_picker.listener.ShortPeriodPickerScrollListener

/**
 * Тестирование слушателя [ShortPeriodPickerScrollListener].
 *
 * @author mb.kruglova
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class ShortPeriodPickerScrollListenerTest {

    private val layoutManager: LinearLayoutManager = mock {
        on { findFirstVisibleItemPosition() }.thenReturn(1)
    }
    private val listAdapter: ShortPeriodPickerListAdapter = mock {
        on { itemCount }.thenReturn(10)
        on { getItemByPosition(any()) }.thenReturn(ShortPeriodPickerItem.YearItem(2024, true, true))
    }
    private val listener = spy(ShortPeriodPickerScrollListener(mock(), 2020, layoutManager, listAdapter))
    private val recyclerView: RecyclerView = mock()

    @Test
    fun `When method onScrolled is performed then findFirstVisibleItemPosition and getItemCount methods invoke`() {
        listener.onScrolled(recyclerView, 0, 0)
        verify(layoutManager).findFirstVisibleItemPosition()
        verify(listAdapter).itemCount
    }
}