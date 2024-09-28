package ru.tensor.sbis.design.selection.ui.list.items.multi.region

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
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.region.RegionSelectorItemModel

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class RegionMultiSelectorItemViewHolderTest {

    @Mock
    private lateinit var meta: SelectorItemMeta

    @Mock
    private lateinit var data: RegionSelectorItemModel

    @Mock
    private lateinit var clickDelegate: SelectionClickDelegate<SelectorItemModel>

    @Mock
    private lateinit var itemView: View

    @Mock
    private lateinit var title: HighlightedTextView

    @Mock
    private lateinit var counter: TextView

    @Mock
    private lateinit var selectionIconClickAreaView: View

    @Mock
    private lateinit var subtitle: TextView

    @Mock
    private lateinit var selectionIcon: TextView

    private lateinit var viewHolder: RegionMultiSelectorItemViewHolder

    @Before
    fun setUp() {
        whenever(data.meta).thenReturn(meta)
        whenever(itemView.findViewById<TextView>(R.id.title)).thenReturn(title)
        whenever(itemView.findViewById<TextView>(R.id.subtitle)).thenReturn(subtitle)
        whenever(itemView.findViewById<View>(R.id.selectionIconClickArea)).thenReturn(selectionIconClickAreaView)
        whenever(itemView.findViewById<TextView>(R.id.counter)).thenReturn(counter)
        whenever(itemView.findViewById<TextView>(R.id.selectionIcon)).thenReturn(selectionIcon)

        viewHolder = RegionMultiSelectorItemViewHolder(itemView, clickDelegate)
    }

    @Test
    fun `When bind() called, then title maxLines equals to MAX_TITLE_LINES`() {
        viewHolder.bind(data)

        verify(title).maxLines = MAX_TITLE_LINES
    }
}