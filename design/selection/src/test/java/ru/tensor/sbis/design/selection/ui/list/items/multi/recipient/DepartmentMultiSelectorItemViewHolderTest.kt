package ru.tensor.sbis.design.selection.ui.list.items.multi.recipient

import android.view.View
import android.widget.TextView
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
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
import ru.tensor.sbis.design.selection.ui.model.recipient.DepartmentSelectorItemModel

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class DepartmentMultiSelectorItemViewHolderTest {

    @Mock
    private lateinit var meta: SelectorItemMeta

    @Mock
    private lateinit var itemView: View

    @Mock
    private lateinit var data: DepartmentSelectorItemModel

    @Mock
    private lateinit var title: HighlightedTextView

    @Mock
    private lateinit var subtitle: TextView

    @Mock
    private lateinit var selectionIcon: TextView

    private lateinit var viewHolder: DepartmentMultiSelectorItemViewHolder

    @Before
    fun setUp() {
        whenever(data.meta).thenReturn(meta)

        whenever(itemView.findViewById<TextView>(R.id.title)).thenReturn(title)
        whenever(itemView.findViewById<TextView>(R.id.subtitle)).thenReturn(subtitle)
        whenever(itemView.findViewById<View>(R.id.selectionIconClickArea)).thenReturn(itemView)
        whenever(itemView.findViewById<TextView>(R.id.selectionIcon)).thenReturn(selectionIcon)

        viewHolder = DepartmentMultiSelectorItemViewHolder(itemView, mock(), mock(), mock())
    }

    @Test
    fun `Given empty subtitle and zero membersCount, when bind() called, then subtitle is gone`() {
        whenever(data.subtitle).doReturn("")
        whenever(data.membersCount).doReturn(0)

        viewHolder.bind(data)

        verify(subtitle).visibility = View.GONE
    }

    @Test
    fun `Given not empty subtitle and zero membersCount, when bind() called, then subtitle is visible`() {
        whenever(data.subtitle).doReturn("hello subtitle")
        whenever(data.membersCount).doReturn(0)

        viewHolder.bind(data)

        verify(subtitle).visibility = View.VISIBLE
    }

    @Test
    fun `Given empty subtitle and not zero membersCount, when bind() called, then subtitle is visible`() {
        whenever(data.subtitle).doReturn("")
        whenever(data.membersCount).doReturn(55)

        viewHolder.bind(data)

        verify(subtitle).visibility = View.VISIBLE
    }

    @Test
    fun `Given not empty subtitle and zero membersCount, when bind() called, then subtitle text is subtitle`() {
        val subtitleText = "hello subtitle"
        whenever(data.subtitle).doReturn(subtitleText)
        whenever(data.membersCount).doReturn(0)

        viewHolder.bind(data)

        verify(subtitle).text = subtitleText
    }

    @Test
    fun `Given empty subtitle and not zero membersCount, when bind() called, then subtitle text is count only`() {
        whenever(data.subtitle).doReturn("")
        whenever(data.membersCount).doReturn(556)

        viewHolder.bind(data)

        verify(subtitle).text = "(556)"
    }

    @Test
    fun `Given not empty subtitle and not zero membersCount, when bind() called, then subtitle is subtitle + count`() {
        whenever(data.subtitle).doReturn("Hello")
        whenever(data.membersCount).doReturn(882)

        viewHolder.bind(data)

        verify(subtitle).text = "Hello (882)"
    }
}
