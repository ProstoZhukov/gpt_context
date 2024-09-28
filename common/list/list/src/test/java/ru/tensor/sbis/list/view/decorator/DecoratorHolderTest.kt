package ru.tensor.sbis.list.view.decorator

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design.list_utils.decoration.SelectionMarkItemDecoration
import ru.tensor.sbis.list.view.utils.layout_manager.SbisGridLayoutManager

@RunWith(MockitoJUnitRunner.StrictStubs::class)
class DecoratorHolderTest {

    private val decoratorHolder = DecoratorHolder()

    private val mockRecyclerView = mock<RecyclerView>()
    private val layoutManager = mock<SbisGridLayoutManager>()
    private val mockLastItemBottomPaddingDecoration = mock<LastItemBottomPaddingDecoration>()
    private val mockDividerItemDecoration = mock<ItemDecoration>()
    private val mockSectionDecoration = mock<ItemDecoration>()
    private val mockStickHeaderItemDecoration = mock<ItemDecoration>()
    private val mockSelectionMarkItemDecoration = mock<SelectionMarkItemDecoration>()

    private val mockFactory = mock<DecoratorFactory> {
        on { lastItemBottomPaddingDecoration() } doReturn mockLastItemBottomPaddingDecoration doReturn mock()
        on { dividerItemDecoration() } doReturn mockDividerItemDecoration
        on { sectionDecoration() } doReturn mockSectionDecoration
        on { stickHeaderItemDecoration() } doReturn mockStickHeaderItemDecoration
        on { selectionMarkItemDecoration() } doReturn mockSelectionMarkItemDecoration
    }

    @Before
    fun setUp() {
        decoratorHolder.addDecorators(
            mockRecyclerView,
            layoutManager,
            sections = mock(),
            spaceBetweenSectionsPx = 123,
            colorProvider = mock(),
            stickyHeaderInterface = mock(),
            factory = mockFactory
        )
    }

    @Test
    fun `Should add decorators initially`() {
        verify(mockRecyclerView, never()).addItemDecoration(mockLastItemBottomPaddingDecoration)
        verify(mockRecyclerView).addItemDecoration(mockDividerItemDecoration)
        verify(mockRecyclerView).addItemDecoration(mockSectionDecoration)
        verify(mockRecyclerView).addItemDecoration(mockStickHeaderItemDecoration)
    }

    @Test
    fun removeLastItemBottomPadding() {
        decoratorHolder.removeLastItemBottomPadding(mockRecyclerView)

        verify(mockRecyclerView).removeItemDecoration(mockLastItemBottomPaddingDecoration)
    }

    @Test
    fun `Do not add last item padding decoration if contain`() {
        clearInvocations(mockRecyclerView)
        //cat
        decoratorHolder.makeSureLastItemPaddingDecoratorIsAdded(mockRecyclerView, hasNav = false, hasFab = true)
        //verify
        val inOrder = inOrder(mockRecyclerView)
        inOrder.verify(mockRecyclerView).removeItemDecoration(mockLastItemBottomPaddingDecoration)
        inOrder.verify(mockRecyclerView).addItemDecoration(mockLastItemBottomPaddingDecoration)
    }

    @Test
    fun `Add last item padding decoration if not contain`() {
        decoratorHolder.removeLastItemBottomPadding(mockRecyclerView)
        clearInvocations(mockRecyclerView)
        //act
        decoratorHolder.makeSureLastItemPaddingDecoratorIsAdded(mockRecyclerView, hasNav = false, hasFab = true)

        //verify
        verify(mockRecyclerView).addItemDecoration(mockLastItemBottomPaddingDecoration)
    }
}