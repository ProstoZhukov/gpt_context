package ru.tensor.sbis.design.selection.ui.list.items.multi

import android.view.View
import android.widget.TextView
import org.mockito.kotlin.*
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.common_views.HighlightedTextView
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * @author ma.kolpakov
 */
@RunWith(JUnitParamsRunner::class)
class DefaultMultiSelectorViewHolderTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var meta: SelectorItemMeta

    @Mock
    private lateinit var data: SelectorItemModel

    @Mock
    private lateinit var itemView: View

    @Mock
    private lateinit var clickDelegate: SelectionClickDelegate<SelectorItemModel>

    @Mock
    private lateinit var selectionIconClickArea: View

    @Mock
    private lateinit var selectionIcon: TextView

    private lateinit var viewHolder: DefaultMultiSelectorViewHolder<SelectorItemModel>

    @Before
    fun setUp() {
        whenever(itemView.findViewById<HighlightedTextView>(R.id.title)).thenReturn(mock())
        whenever(itemView.findViewById<TextView>(R.id.subtitle)).thenReturn(mock())
        whenever(itemView.findViewById<View>(R.id.selectionIconClickArea)).thenReturn(selectionIconClickArea)
        whenever(itemView.findViewById<TextView>(R.id.selectionIcon)).thenReturn(selectionIcon)

        viewHolder = DefaultMultiSelectorViewHolder(itemView, clickDelegate)
    }

    @Test
    fun `When view holder created, then selection icon click area should get click listener`() {
        verify(selectionIconClickArea).setOnClickListener(any())
    }

    @Parameters("true", "false")
    @Test
    fun `When view holder get a data, then root view should get selection status`(isSelected: Boolean) {
        whenever(data.meta).thenReturn(meta)
        whenever(meta.isSelected).thenReturn(isSelected)

        viewHolder.bind(data)

        verify(itemView).isSelected = isSelected
    }
}