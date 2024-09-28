package ru.tensor.sbis.design.selection.ui.list.items.single

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
import ru.tensor.sbis.common_views.SearchSpan
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class DefaultSingleSelectorViewHolderTest {

    private val titleText = "Title text"
    private val subtitleText = "Subtitle text"

    @Mock
    private lateinit var meta: SelectorItemMeta

    @Mock
    private lateinit var data: SelectorItemModel

    @Mock
    private lateinit var itemView: View

    @Mock
    private lateinit var title: HighlightedTextView

    @Mock
    private lateinit var subtitle: TextView

    private lateinit var viewHolder: DefaultSingleSelectorViewHolder<SelectorItemModel>

    @Before
    fun setUp() {
        whenever(data.meta).thenReturn(meta)

        whenever(itemView.findViewById<TextView>(R.id.title)).thenReturn(title)
        whenever(itemView.findViewById<TextView>(R.id.subtitle)).thenReturn(subtitle)

        viewHolder = DefaultSingleSelectorViewHolder(itemView)
    }

    @Test
    fun `When view holder get a data without queryRange, then title should get a title text`() {
        whenever(data.title).thenReturn(titleText)

        viewHolder.bind(data)

        verify(title).setTextWithHighlight(titleText, emptyList<SearchSpan>(), "")
    }

    @Test
    fun `When view holder get a data with queryRange, then title should get a title text`() {
        val testSearchSpan = SearchSpan(1, 4)
        whenever(data.title).thenReturn(titleText)
        whenever(meta.queryRanges).thenReturn(listOf(1..4))

        viewHolder.bind(data)

        verify(title).setTextWithHighlight(titleText, listOf(testSearchSpan), "")
    }

    @Test
    fun `When view holder get a data, then title should get a subtitle text`() {
        whenever(data.subtitle).thenReturn(subtitleText)

        viewHolder.bind(data)

        verify(subtitle).text = subtitleText
    }

    @Test
    fun `When subtitle is null or empty, then subtitle view should be gone`() {
        viewHolder.bind(data)

        verify(subtitle).visibility = View.GONE
    }

    @Test
    fun `When subtitle is not null or empty, then subtitle view should be visible`() {
        whenever(data.subtitle).thenReturn(subtitleText)

        viewHolder.bind(data)

        verify(subtitle).visibility = View.VISIBLE
    }
}