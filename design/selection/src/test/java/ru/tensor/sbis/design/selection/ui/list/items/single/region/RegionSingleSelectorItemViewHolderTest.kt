package ru.tensor.sbis.design.selection.ui.list.items.single.region

import android.view.View
import android.widget.TextView
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common_views.HighlightedTextView
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.ui.list.items.single.MAX_TITLE_LINES
import ru.tensor.sbis.design.selection.ui.model.region.RegionSelectorItemModel

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class RegionSingleSelectorItemViewHolderTest {

    @Mock
    private lateinit var meta: SelectorItemMeta

    @Mock
    private lateinit var data: RegionSelectorItemModel

    @Mock
    private lateinit var itemView: View

    @Mock
    private lateinit var title: HighlightedTextView

    @Mock
    private lateinit var counter: TextView

    @Mock
    private lateinit var marker: View

    @Mock
    private lateinit var subtitle: TextView

    private lateinit var viewHolder: RegionSingleSelectorItemViewHolder

    @Before
    fun setUp() {
        whenever(data.meta).thenReturn(meta)
        whenever(itemView.findViewById<TextView>(R.id.title)).thenReturn(title)
        whenever(itemView.findViewById<TextView>(R.id.subtitle)).thenReturn(subtitle)
        whenever(itemView.findViewById<TextView>(R.id.counter)).thenReturn(counter)
        whenever(itemView.findViewById<View>(R.id.marker)).thenReturn(marker)

        viewHolder = RegionSingleSelectorItemViewHolder(itemView)
    }

    @Test
    fun `When bind() called, then title maxLines equals to MAX_TITLE_LINES`() {
        viewHolder.bind(data)

        verify(title).maxLines = MAX_TITLE_LINES
    }
}
