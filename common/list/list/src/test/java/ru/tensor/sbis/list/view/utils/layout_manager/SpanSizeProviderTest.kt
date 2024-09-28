package ru.tensor.sbis.list.view.utils.layout_manager

import androidx.recyclerview.widget.GridLayoutManager
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import junit.framework.Assert.assertEquals
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.list.view.section.SectionsHolder
import ru.tensor.sbis.list.view.utils.BottomLoadMoreProgressHelper
import ru.tensor.sbis.list.view.utils.TopLoadMoreProgressHelper

@RunWith(JUnitParamsRunner::class)
class SpanSizeProviderTest {

    private val sectionsHolder = mock<SectionsHolder>()
    private val bottomLoadMoreProgressHelper = mock<BottomLoadMoreProgressHelper>()
    private val topLoadMoreProgressHelper = mock<TopLoadMoreProgressHelper>()
    private val checkIsTopProgress = mock<(Int) -> Boolean>()
    private val getActualPosition = mock<(Int) -> Int>()
    private val checkIsBottomProgress = mock<(Int) -> Boolean>()
    private val spanSizeProvider = SpanSizeProvider(
        sectionsHolder,
        bottomLoadMoreProgressHelper,
        topLoadMoreProgressHelper,
        checkIsTopProgress,
        getActualPosition,
        checkIsBottomProgress
    )
    private val gridLayoutManager = mock<GridLayoutManager>()

    @Test
    @Parameters(
        "0, true, 4, false, 4, 2, 4",
        "0, false, 4, true, 4, 2, 4",
        "0, false, 4, false, 4, 2, 2",
        "0, false, 4, false, 4, 5, 4",
        "10, true, 5, false, 4, 2, 4",
        "10, false, 5, true, 4, 2, 4",
        "10, false, 5, false, 4, 2, 2",
        //Следующие для ячеек, у которых размер болше половины
        "0, true, 4, false, 4, 3, 4",
        "0, false, 4, true, 4, 3, 4",
        "0, false, 4, false, 4, 3, 2",
        "0, false, 4, false, 4, 5, 4",
        "10, true, 5, false, 4, 3, 4",
        "10, false, 5, true, 4, 3, 4",
        "10, false, 5, false, 4, 3, 2",
    )
    fun provide(
        position: Int,
        checkIsTopProgress: Boolean,
        actualPosition: Int,
        checkIsBottomProgress: Boolean,
        spanCount: Int,
        spanSize: Int,
        result: Int
    ) {
        whenever(checkIsTopProgress(position)) doReturn checkIsTopProgress
        whenever(getActualPosition(position)) doReturn actualPosition
        whenever(checkIsBottomProgress(position)) doReturn checkIsBottomProgress
        whenever(sectionsHolder.getSpanSize(actualPosition, spanCount)) doReturn spanSize
        whenever(gridLayoutManager.spanCount) doReturn spanCount

        assertEquals(result, spanSizeProvider.provide(position, gridLayoutManager))
    }
}