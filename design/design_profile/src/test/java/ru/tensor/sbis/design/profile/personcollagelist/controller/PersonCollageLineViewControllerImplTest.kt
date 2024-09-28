package ru.tensor.sbis.design.profile.personcollagelist.controller

import android.content.Context
import android.view.View
import android.view.View.MeasureSpec.getMode
import android.view.View.MeasureSpec.getSize
import android.view.View.resolveSize
import android.view.ViewGroup
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.tensor.sbis.common.testing.doReturn
import ru.tensor.sbis.common.testing.mockStatic
import ru.tensor.sbis.design.profile.mockPhotoSize
import ru.tensor.sbis.design.profile.personcollagelist.collagechildrenmanager.PersonCollageLineViewChildrenManagerImpl
import ru.tensor.sbis.design.profile_decl.person.PersonData
import kotlin.math.min

private const val WIDTH_SPEC = 1
private const val HEIGHT_SPEC = 2
private const val MAX_WIDTH = 100
private const val MAX_HEIGHT = 20

/**
 * @author us.bessonov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class PersonCollageLineViewControllerImplTest {

    @Mock
    private lateinit var mockCollageView: ViewGroup

    @Mock
    private lateinit var mockContext: Context

    private var mockChildrenManager: PersonCollageLineViewChildrenManagerImpl = mock()

    private lateinit var controller: PersonCollageLineViewControllerImpl

    @Before
    fun setUp() {
        whenever(mockCollageView.context).thenReturn(mockContext)
        controller = PersonCollageLineViewControllerImpl(mockChildrenManager)
        controller.setCollageView(mockCollageView)
    }

    @Test
    fun `When data list is changed, then view re-layout is requested`() {
        val list = listOf(PersonData())
        controller.setDataList(list)
        controller.setDataList(list)

        verify(mockCollageView).requestLayout()
    }

    @Test
    fun `When size is changed, then view re-layout is requested`() {
        val mockPhotoSize1 = mockPhotoSize(mockContext, 11)
        val mockPhotoSize2 = mockPhotoSize(mockContext, 22)
        controller.setSize(mockPhotoSize1)
        controller.setSize(mockPhotoSize2)

        verify(mockCollageView, times(2)).requestLayout()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `When max visible count is negative, then throws an exception`() {
        controller.setMaxVisibleCount(-1)
    }

    @Test
    fun `When max visible count is changed, then view re-layout is requested`() {
        controller.setMaxVisibleCount(3)
        controller.setMaxVisibleCount(3)

        verify(mockCollageView).requestLayout()
    }

    @Test
    fun `When total count is changed, then view re-layout is requested`() {
        controller.setTotalCount(3)
        controller.setTotalCount(3)

        verify(mockCollageView).requestLayout()
    }

    @Test
    fun `When collage is measured, and there is enough space available, then all children are displayed, and measured dimension is as expected`() {
        val count = 5
        val data = (0 until count).map { PersonData() }

        controller.setTotalCount(9)
        controller.setDataList(data)
        val measuredDimension = controller.performMeasure(WIDTH_SPEC, HEIGHT_SPEC, 0)

        verify(mockChildrenManager).updateChildren(count, 4, data)
        assertEquals(MeasuredDimension(72, MAX_HEIGHT), measuredDimension)
    }

    @Test
    fun `When collage is measured, then its paddings are taken into account when size is determined`() {
        val count = 5
        val data = (0 until count).map { PersonData() }
        whenever(mockCollageView.paddingLeft).thenReturn(1)
        whenever(mockCollageView.paddingTop).thenReturn(2)
        whenever(mockCollageView.paddingRight).thenReturn(3)
        whenever(mockCollageView.paddingBottom).thenReturn(4)

        controller.setTotalCount(9)
        controller.setDataList(data)
        val measuredDimension = controller.performMeasure(WIDTH_SPEC, HEIGHT_SPEC, 0)

        verify(mockChildrenManager).updateChildren(count, 4, data)
        assertEquals(MeasuredDimension(54, MAX_HEIGHT), measuredDimension)
    }

    @Test
    fun `When collage is measured, then its width is not less than minimal width`() {
        val count = 5
        val data = (0 until count).map { PersonData() }
        val minWidth = 90

        controller.setDataList(data)
        val measuredDimension = controller.performMeasure(WIDTH_SPEC, HEIGHT_SPEC, minWidth)

        verify(mockChildrenManager).updateChildren(count, 0, data)
        assertEquals(MeasuredDimension(minWidth, MAX_HEIGHT), measuredDimension)
    }

    @Test
    fun `When collage is measured, and there is not enough space available, then only children that fit are displayed, and measured dimension is as expected`() {
        val count = 10
        val data = (0 until count).map { PersonData() }

        controller.setDataList(data)
        val measuredDimension = controller.performMeasure(WIDTH_SPEC, HEIGHT_SPEC, 0)

        verify(mockChildrenManager).updateChildren(7, 0, data)
        assertEquals(MeasuredDimension(98, MAX_HEIGHT), measuredDimension)
    }

    @Test
    fun `When collage is measured, and data list is empty, then its width is not less than minimal width, and its height is not zero`() {
        val minWidth = 50

        val measuredDimension = controller.performMeasure(WIDTH_SPEC, HEIGHT_SPEC, minWidth)

        assertEquals(MeasuredDimension(minWidth, MAX_HEIGHT), measuredDimension)
    }

    @Test
    fun `When collage is measured, and item size is specified, then it is properly applied`() {
        controller.setSize(mockPhotoSize(mockContext, 40))
        controller.setDataList((0 until 5).map { PersonData() })
        val measuredDimension = controller.performMeasure(WIDTH_SPEC, HEIGHT_SPEC, 0)

        assertEquals(MeasuredDimension(94, 40), measuredDimension)
    }

    companion object {

        var mockedStaticMeasureSpec: MockedStatic<View.MeasureSpec>? = null
        var mockedStaticView: MockedStatic<View>? = null

        @BeforeClass
        @JvmStatic
        fun mockStatic() {
            mockedStaticMeasureSpec = mockStatic {
                on<Int> { getMode(WIDTH_SPEC) } doReturn View.MeasureSpec.AT_MOST
                on<Int> { getMode(HEIGHT_SPEC) } doReturn View.MeasureSpec.AT_MOST
                on<Int> { getSize(WIDTH_SPEC) } doReturn MAX_WIDTH
                on<Int> { getSize(HEIGHT_SPEC) } doReturn MAX_HEIGHT
            }
            mockedStaticView = mockStatic {
                on<Int> { resolveSize(anyInt(), anyInt()) } doAnswer {
                    min(
                        it.getArgument(0) as Int,
                        if (it.getArgument(1) as Int == WIDTH_SPEC) MAX_WIDTH else MAX_HEIGHT
                    )
                }
            }
        }

        @AfterClass
        @JvmStatic
        fun closeMocks() {
            mockedStaticMeasureSpec?.close()
            mockedStaticView?.close()
        }
    }

}