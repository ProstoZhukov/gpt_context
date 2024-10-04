package ru.tensor.sbis.design.list_utils.util

import android.content.res.Resources
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

private const val HEADER_ELEVATION = 3f

/**
 * Тесты инструмента для отображения тени под шапкой списка.
 *
 * @author us.bessonov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class ListHeaderElevationHelperTest {

    @Mock
    private lateinit var mockHeaderView: View

    @Mock
    private lateinit var mockRecyclerView: RecyclerView

    @Mock
    private lateinit var mockResources: Resources

    private lateinit var elevationHelper: ListHeaderElevationHelper

    @Before
    fun setUp() {
        elevationHelper = ListHeaderElevationHelper(mockHeaderView)
        whenever(mockHeaderView.resources).thenReturn(mockResources)
        whenever(mockResources.getDimension(anyInt())).thenReturn(HEADER_ELEVATION)
    }

    @Test
    fun `When list is scrolled to the top, then header shadow should be hidden`() {
        whenever(mockHeaderView.elevation).thenReturn(HEADER_ELEVATION)
        whenever(mockRecyclerView.canScrollVertically(SCROLL_DIRECTION_UP)).thenReturn(false)

        elevationHelper.onScrolled(mockRecyclerView, 0, -10)

        verify(mockHeaderView).elevation = 0f
    }

    @Test
    fun `When list can be scrolled to the top, then header shadow should be shown`() {
        whenever(mockHeaderView.elevation).thenReturn(0f)
        whenever(mockRecyclerView.canScrollVertically(SCROLL_DIRECTION_UP)).thenReturn(true)

        elevationHelper.onScrolled(mockRecyclerView, 0, 10)

        verify(mockHeaderView).elevation = HEADER_ELEVATION
    }

    @Test
    fun `When header view changed and previous header has shadow, then previous header shadow should be hidden and new header shadow should be shown`() {
        whenever(mockHeaderView.elevation).thenReturn(HEADER_ELEVATION)
        val mockNewHeaderView = mock<View> {
            on { resources } doReturn mockResources
        }

        elevationHelper.setHeaderView(mockNewHeaderView)

        verify(mockHeaderView).elevation = 0f
        verify(mockNewHeaderView).elevation = HEADER_ELEVATION
    }

}