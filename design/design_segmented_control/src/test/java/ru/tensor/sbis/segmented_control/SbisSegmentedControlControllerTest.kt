package ru.tensor.sbis.segmented_control

import android.content.Context
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.tensor.sbis.segmented_control.control.api.SbisSegmentedControlController
import ru.tensor.sbis.segmented_control.item.SbisSegmentedControlItem
import ru.tensor.sbis.segmented_control.item.api.SbisSegmentedControlItemFactory
import ru.tensor.sbis.segmented_control.item.models.SbisSegmentedControlItemModel
import ru.tensor.sbis.segmented_control.item.models.SbisSegmentedControlTitle
import ru.tensor.sbis.segmented_control.utils.SegmentedControlStyleHolder

/**
 * @author ps.smirnyh
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class SbisSegmentedControlControllerTest {

    @Mock
    private lateinit var segmentedControl: SbisSegmentedControl

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var segmentedControlItem: SbisSegmentedControlItem

    @Mock
    private lateinit var segmentedControlItemFactory: SbisSegmentedControlItemFactory

    @Mock
    private lateinit var segmentedControlStyleHolder: SegmentedControlStyleHolder

    private lateinit var segmentedControlController: SbisSegmentedControlController

    @Before
    fun setUp() {
        segmentedControlController =
            SbisSegmentedControlController(segmentedControlStyleHolder, segmentedControlItemFactory)
        segmentedControlController.attach(segmentedControl, null, 0, 0)
    }

    @Test
    fun `When pass the list of models then will be add number of size list segments`() {
        whenever(segmentedControl.context).thenReturn(mockContext)
        whenever(segmentedControlItemFactory.createItem(any(), any(), any(), any(), any())).thenReturn(
            segmentedControlItem
        )
        doNothing().whenever(segmentedControlItem).setOnClickListener(any())

        segmentedControlController.setSegments(
            listOf(
                SbisSegmentedControlItemModel(title = SbisSegmentedControlTitle(""))
            )
        )
        verify(segmentedControl, times(1)).addView(any())
    }

    @Test
    fun `When we set the number of the selected segment then the number of the selected segment is equal to the passed`() {
        val segmentIndex = 2
        segmentedControlController.listSegments = MutableList(3) { segmentedControlItem }
        segmentedControlController.setSelectedSegmentIndex(segmentIndex, false)
        assert(segmentedControlController.selectedSegmentIndex == segmentIndex)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `When we set the number of the selected segment more quantity segments then throw exception IndexOutOfBounds`() {
        val segmentIndex = 2
        segmentedControlController.listSegments = MutableList(2) { segmentedControlItem }
        segmentedControlController.setSelectedSegmentIndex(segmentIndex, false)
    }

    @Test
    fun `When we set change segment listener then he must called on change selected index`() {
        val segmentIndex = 1
        val segmentModel = SbisSegmentedControlItemModel(title = SbisSegmentedControlTitle(""))
        whenever(segmentedControl.context).thenReturn(mockContext)
        whenever(segmentedControl.addView(any())).then {
            segmentedControlController.listSegments.add(
                segmentedControlItem
            )
        }
        whenever(segmentedControl.changeSelectedSegment(any(), any())).then {
            segmentedControlController.onChangedSelectedSegment()
        }
        whenever(segmentedControlItemFactory.createItem(any(), any(), any(), any(), any())).thenReturn(
            segmentedControlItem
        )
        whenever(segmentedControlItem.model).thenReturn(segmentModel)
        doNothing().whenever(segmentedControlItem).setOnClickListener(any())

        segmentedControlController.onSelectedSegmentChangedListener = mock()
        segmentedControlController.setSegments(List(2) { segmentModel })
        segmentedControlController.setSelectedSegmentIndex(segmentIndex, false)
        verify(segmentedControlController.onSelectedSegmentChangedListener, times(1))?.invoke(any(), any())
    }
}