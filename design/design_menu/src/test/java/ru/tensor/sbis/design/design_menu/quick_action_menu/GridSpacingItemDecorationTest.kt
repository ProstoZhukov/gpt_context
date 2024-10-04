package ru.tensor.sbis.design.design_menu.quick_action_menu

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.mockk.every
import io.mockk.mockk
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.common.testing.params

/**
 * Тест декоратора списка меню быстрых действий.
 *
 * @author ra.geraskin
 */
private const val SPACING_VALUE = 50
private const val SINGLE_COLUMN_LIST_SPAN_COUNT = 1
private const val DOUBLE_COLUMN_LIST_SPAN_COUNT = 2

@RunWith(JUnitParamsRunner::class)
class GridSpacingItemDecorationTest {

    private val recyclerViewMock: RecyclerView = mockk()
    private val childView: View = mockk()

    private fun setPositionToRecycler(position: Int) {
        every { recyclerViewMock.getChildAdapterPosition(childView) } returns (position)
    }

    @Suppress("unused")
    private fun testParamsForTopSpacingZeroCheck() =
        params {
            add(SINGLE_COLUMN_LIST_SPAN_COUNT, 0)
            add(DOUBLE_COLUMN_LIST_SPAN_COUNT, 0)
            add(DOUBLE_COLUMN_LIST_SPAN_COUNT, 1)
        }

    @Test
    @Parameters(method = "testParamsForTopSpacingZeroCheck")
    fun `Top list items have zero top padding`(spanCount: Int, position: Int) {
        val decorator = GridSpacingItemDecoration(spanCount, SPACING_VALUE)
        val rect = Rect()

        // act
        setPositionToRecycler(position)
        decorator.getItemOffsets(rect, childView, recyclerViewMock, RecyclerView.State())

        // verify
        Assert.assertEquals(rect.top, 0)
    }

    @Suppress("unused")
    private fun testParamsForHorizontalOffsetCheck() =
        params {
            add(DOUBLE_COLUMN_LIST_SPAN_COUNT, 1)
            add(DOUBLE_COLUMN_LIST_SPAN_COUNT, 3)
            add(DOUBLE_COLUMN_LIST_SPAN_COUNT, 5)
        }

    @Test
    @Parameters(method = "testParamsForHorizontalOffsetCheck")
    fun `Sum of horizontal paddings of elements of one row in a two-column list is equal to the SPACING value`(
        spanCount: Int,
        position: Int
    ) {
        val decorator = GridSpacingItemDecoration(spanCount, SPACING_VALUE)
        val rectLeft = Rect()
        val rectRight = Rect()

        // act
        setPositionToRecycler(position)
        decorator.getItemOffsets(rectLeft, childView, recyclerViewMock, RecyclerView.State())
        setPositionToRecycler(position + 1)
        decorator.getItemOffsets(rectRight, childView, recyclerViewMock, RecyclerView.State())

        // verify
        Assert.assertEquals(rectLeft.right + rectRight.left, SPACING_VALUE)
    }

}