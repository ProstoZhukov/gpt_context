package ru.tensor.sbis.design.selection.ui.list

import org.mockito.kotlin.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.selection.bl.contract.listener.ClickHandleStrategy
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItemMeta
import ru.tensor.sbis.design.selection.ui.list.items.SelectorCustomisation
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.list.view.item.ViewHolderHelper

@RunWith(MockitoJUnitRunner.StrictStubs::class)
class ListItemMapperTest {

    @Mock
    private lateinit var meta: SelectorItemMeta

    @Mock
    private lateinit var data: SelectorItemModel

    @Mock
    private lateinit var mockSelectorCustomisation: SelectorCustomisation

    @Mock
    private lateinit var mockViewHolderHelpers: Map<Any, ViewHolderHelper<SelectorItemModel, *>>

    @Mock
    private lateinit var mockClickDelegate: SelectionClickDelegate<SelectorItemModel>

    @InjectMocks
    private lateinit var mapper: ListItemMapper

    @Before
    fun setUp() {
        whenever(data.meta).thenReturn(meta)
        whenever(mockSelectorCustomisation.getViewHolderType(any())).thenReturn(Unit)
        whenever(mockViewHolderHelpers[Unit]).thenReturn(mock())
    }

    @Test
    fun `Given ClickHandleStrategy DEFAULT, when toItem() called, then clickDelegate is set`() {
        whenever(meta.handleStrategy).thenReturn(ClickHandleStrategy.DEFAULT)
        val item = mapper.toItem(data)

        item.clickAction()

        verify(mockClickDelegate).onItemClicked(data)
    }

    @Test
    fun `Given ClickHandleStrategy COMPLETE_SELECTION, when toItem() called, then clickDelegate is set`() {
        whenever(meta.handleStrategy).thenReturn(ClickHandleStrategy.COMPLETE_SELECTION)
        val item = mapper.toItem(data)

        item.clickAction()

        verify(mockClickDelegate).onItemClicked(data)
    }

    @Test
    fun `Given ClickHandleStrategy IGNORE, when toItem() called, then clickDelegate is not set`() {
        whenever(meta.handleStrategy).thenReturn(ClickHandleStrategy.IGNORE)
        val item = mapper.toItem(data)

        item.clickAction()

        verify(mockClickDelegate, never()).onItemClicked(any())
    }
}
