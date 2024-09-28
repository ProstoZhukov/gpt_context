package ru.tensor.sbis.list.view.utils.layout_manager

import androidx.recyclerview.widget.GridLayoutManager
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.junit.Test
import ru.tensor.sbis.list.view.section.SectionsHolder
import ru.tensor.sbis.list.view.utils.BottomLoadMoreProgressHelper

internal class ItemSpanSizeLookupTest {

    private val sectionsHolder = mock<SectionsHolder>()
    private val bottomLoadMoreProgressHelper = mock<BottomLoadMoreProgressHelper>()
    private val gridLayoutManager = mock<GridLayoutManager>()
    private val spanCountProvider = mock<SpanSizeProvider>()
    private val itemSpanSizeLookup = ItemSpanSizeLookup(
        sectionsHolder,
        bottomLoadMoreProgressHelper,
        mock(),
        spanCountProvider
    ).apply {
        setGridLayoutManager(gridLayoutManager)
    }

    @Test
    fun getSpanSize() {
        val position = 123
        itemSpanSizeLookup.getSpanSize(position)

        verify(spanCountProvider).provide(position, gridLayoutManager)
    }
}