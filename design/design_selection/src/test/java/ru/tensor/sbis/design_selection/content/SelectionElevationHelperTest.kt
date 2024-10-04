package ru.tensor.sbis.design_selection.content

import android.content.res.Resources
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.design_selection.ui.content.utils.SCROLL_DIRECTION_UP
import ru.tensor.sbis.design_selection.ui.content.utils.SelectionElevationHelper

/**
 * Тесты вспомогательной реализации [SelectionElevationHelper] для наложения тени на список компонента выбора.
 *
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SelectionElevationHelperTest {

    private lateinit var helper: SelectionElevationHelper

    @Before
    fun setUp() {
        helper = SelectionElevationHelper()
    }

    @Test
    fun `When set first shadow view, then are no interactions`() {
        val view = mock<View>()

        helper.setShadowView(view)

        verifyNoMoreInteractions(view)
    }

    @Test
    fun `When set second view and first doesn't have elevation, then are no interactions on two views`() {
        val view1 = mock<View> {
            on { this.elevation } doReturn 0f
        }
        val view2 = mock<View>()

        helper.setShadowView(view1)
        helper.setShadowView(view2)

        verify(view1).elevation
        verifyNoMoreInteractions(view1)
        verifyNoMoreInteractions(view2)
    }

    @Test
    fun `When set second view and first has elevation, then set shadow elevation to second view and clear first`() {
        val view1 = mock<View> {
            on { this.elevation } doReturn 1f
        }
        val resourcesElevation = 10f
        val mockResources = mock<Resources> {
            on { getDimension(any()) } doReturn resourcesElevation
        }
        val view2 = mock<View> {
            on { resources } doReturn mockResources
        }

        helper.setShadowView(view1)
        helper.setShadowView(view2)

        verify(view1).elevation = 0f
        verify(view2).elevation = resourcesElevation
    }

    @Test
    fun `When view has elevation and list can't scroll to up, then drop elevation`() {
        val view = mock<View> {
            on { this.elevation } doReturn 1f
        }
        val recycler = mock<RecyclerView> {
            on { canScrollVertically(SCROLL_DIRECTION_UP) } doReturn false
        }

        helper.setShadowView(view)
        helper.onScrolled(recycler, 0, 0)

        verify(view).elevation = 0f
        verify(recycler).canScrollVertically(SCROLL_DIRECTION_UP)
    }

    @Test
    fun `When view has elevation and list can scroll to up, then don't set elevation`() {
        val view = mock<View> {
            on { this.elevation } doReturn 1f
        }
        val recycler = mock<RecyclerView> {
            on { canScrollVertically(SCROLL_DIRECTION_UP) } doReturn true
        }

        helper.setShadowView(view)
        helper.onScrolled(recycler, 0, 0)

        verify(view, never()).elevation = any()
    }

    @Test
    fun `When view haven't elevation, list can scroll to up and state isn't idle, then set elevation`() {
        val resourcesElevation = 10f
        val mockResources = mock<Resources> {
            on { getDimension(any()) } doReturn resourcesElevation
        }
        val view = mock<View> {
            on { this.elevation } doReturn 0f
            on { resources } doReturn mockResources
        }
        val recycler = mock<RecyclerView> {
            on { canScrollVertically(SCROLL_DIRECTION_UP) } doReturn true
            on { scrollState } doReturn RecyclerView.SCROLL_STATE_SETTLING
        }

        helper.setShadowView(view)
        helper.onScrolled(recycler, 0, 0)

        verify(view).elevation = resourcesElevation
    }

    @Test
    fun `When view haven't elevation, list can scroll to up and state is idle, then don't set elevation`() {
        val view = mock<View> {
            on { this.elevation } doReturn 0f
        }
        val recycler = mock<RecyclerView> {
            on { canScrollVertically(SCROLL_DIRECTION_UP) } doReturn true
            on { scrollState } doReturn RecyclerView.SCROLL_STATE_IDLE
        }

        helper.setShadowView(view)
        helper.onScrolled(recycler, 0, 0)

        verify(view, never()).elevation = any()
    }
}