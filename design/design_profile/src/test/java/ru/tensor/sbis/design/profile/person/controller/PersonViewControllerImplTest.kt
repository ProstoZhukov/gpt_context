package ru.tensor.sbis.design.profile.person.controller

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.ViewGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.tensor.sbis.design.profile.imageview.PersonImageView
import ru.tensor.sbis.design.profile.mockPhotoSize
import ru.tensor.sbis.design.profile.person.ActivityStatusDrawable
import ru.tensor.sbis.design.profile.person.ActivityStatusStyleHolder
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile_decl.person.CompanyData
import ru.tensor.sbis.design.profile_decl.person.DepartmentData
import ru.tensor.sbis.design.profile_decl.person.GroupData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.person.Shape
import ru.tensor.sbis.design.utils.image_loading.ImageUrl
import ru.tensor.sbis.design.utils.image_loading.ViewImageLoader
import ru.tensor.sbis.person_decl.profile.PersonActivityStatusNotifier
import ru.tensor.sbis.person_decl.profile.model.ActivityStatus
import ru.tensor.sbis.person_decl.profile.model.ProfileActivityStatus
import java.util.UUID

private const val BACKGROUND_COLOR = Color.CYAN
private const val TOOLBAR_PADDING = 6
private const val MAX_SIZE_WITH_ACTIVITY_STATUS = 50

/**
 * @author us.bessonov
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class PersonViewControllerImplTest {

    @Mock
    private lateinit var mockPersonView: PersonView

    @Mock
    private lateinit var mockActivityStatusDrawable: ActivityStatusDrawable

    @Mock
    private lateinit var mockLayoutParams: ViewGroup.LayoutParams

    @Mock
    private lateinit var mockPersonImageView: PersonImageView

    @Mock
    private lateinit var mockImageLoader: ViewImageLoader

    @Mock
    private lateinit var mockActivityStatusNotifier: PersonActivityStatusNotifier

    @Mock
    private lateinit var mockContext: Context

    private lateinit var controller: PersonViewControllerImpl

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val mockDisplayMetrics = mock<DisplayMetrics>().apply {
            density = 1f
        }
        val mockResources = mock<Resources> {
            on { displayMetrics } doReturn mockDisplayMetrics
        }
        whenever(mockPersonView.layoutParams).thenReturn(mockLayoutParams)
        whenever(mockPersonView.context).thenReturn(mockContext)
        whenever(mockPersonView.resources).thenReturn(mockResources)
        whenever(mockActivityStatusDrawable.styleHolder).thenReturn(mock())
        controller = PersonViewControllerImpl(
            MAX_SIZE_WITH_ACTIVITY_STATUS,
            TOOLBAR_PADDING,
            0,
            BACKGROUND_COLOR,
            mockActivityStatusNotifier,
            { _: Int -> mockImageLoader }
        ) { _, _ -> mockPersonImageView }
        controller.init(mockPersonView, mockActivityStatusDrawable)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `When department data with no person is set, then it is marked as stub`() {
        val data = DepartmentData()

        controller.setData(data)

        assertTrue(data.isStub)
    }

    @Test
    fun `When company data is set, then background color is set to transparent`() {
        val data = CompanyData(null)

        controller.setData(data)

        verify(mockPersonImageView).setShapeBackgroundColor(Color.TRANSPARENT)
    }

    @Test
    fun `When other than company data is set, then background color is set to opaque`() {
        val data = PersonData(null)

        controller.setData(data)

        verify(mockPersonImageView).setShapeBackgroundColor(BACKGROUND_COLOR)
    }

    @Test
    fun `When data is set, then it is applied to image view and used in image loader`() {
        val photoUrl = "photoUrl"
        val data = PersonData(null, photoUrl)

        controller.setData(data)

        verify(mockPersonImageView).setData(data)
        verify(mockImageLoader).setImages(listOf(ImageUrl(photoUrl)))
    }

    @Test
    fun `When view size suitable for small activity status size is set, then activity status size is updated accordingly`() {
        val smallStatusSize = 11
        val viewSize = 20
        val mockStyleHolder = mock<ActivityStatusStyleHolder> {
            on { statusSizeSmall } doReturn smallStatusSize
        }
        whenever(mockActivityStatusDrawable.styleHolder).thenReturn(mockStyleHolder)
        whenever(mockPersonView.width).thenReturn(viewSize)

        controller.onSizeChanged(viewSize)

        verify(mockActivityStatusDrawable).size = smallStatusSize
    }

    @Test
    fun `When view size suitable for medium activity status size is set, then activity status size is updated accordingly`() {
        val mediumStatusSize = 22
        val viewSize = 40
        val mockStyleHolder = mock<ActivityStatusStyleHolder> {
            on { statusSizeMedium } doReturn mediumStatusSize
        }
        whenever(mockActivityStatusDrawable.styleHolder).thenReturn(mockStyleHolder)
        whenever(mockPersonView.width).thenReturn(viewSize)

        controller.onSizeChanged(viewSize)

        verify(mockActivityStatusDrawable).size = mediumStatusSize
    }

    @Test
    fun `When photo size is set, then it is updated in image view, and layout requested`() {
        val imageSizePx = 123
        val initialsSize = 44f
        val mockPhotoSize = mockPhotoSize(mockContext, imageSizePx) {
            on { getInitialsTextSize(any()) } doReturn initialsSize
        }
        whenever(mockPersonView.measuredWidth).thenReturn(99)

        controller.setSize(mockPhotoSize)

        verify(mockPersonImageView).setPhotoSize(imageSizePx)
        verify(mockPersonImageView).initialsTextSize = initialsSize
        verify(mockPersonView).requestLayout()
        verify(mockPersonView, times(2)).invalidate()
    }

    @Test
    fun `When too big photo size is set, then activity status is hidden`() {
        val imageSizePx = 123
        val mockPhotoSize = mockPhotoSize(mockContext, imageSizePx)

        controller.setSize(mockPhotoSize)

        verify(mockActivityStatusDrawable).setVisible(false, false)
    }

    @Test
    fun `When not too big photo size is set, then activity status is shown`() {
        val imageSizePx = 23
        val mockPhotoSize = mockPhotoSize(mockContext, imageSizePx)

        controller.setSize(mockPhotoSize)

        verify(mockActivityStatusDrawable).setVisible(true, false)
    }

    @Test
    fun `When bigger preview is required, then data is reset`() {
        val data = PersonData(photoUrl = "https://service/previewer/r/%d/%d/photo")
        val mockOldPhotoSize = mockPhotoSize(mockContext, 23)
        val mockNewPhotoSize = mockPhotoSize(mockContext, 123)

        controller.setSize(mockOldPhotoSize)
        controller.setData(data)
        controller.setSize(mockNewPhotoSize)

        verify(mockPersonImageView, times(2)).setData(data)
    }

    @Test
    fun `When shape is set, then it is applied to image view`() {
        val shape = Shape.SQUARE

        controller.setShape(shape)

        verify(mockPersonImageView).setShape(shape)
    }

    @Test
    fun `When activity status is enabled, but not supported, then UNKNOWN status is displayed`() {
        val data = GroupData(null)

        controller.setData(data)
        controller.setHasActivityStatus(true)

        verify(mockActivityStatusDrawable).setActivityStatus(ActivityStatus.UNKNOWN, false)
    }

    @Test
    fun `When activity status is enabled, then expected status is displayed`() {
        val uuid = UUID.randomUUID()
        val data = PersonData(uuid)
        val status = ActivityStatus.ONLINE_WORK
        whenever(mockActivityStatusNotifier.getStatus(uuid)).thenReturn(ProfileActivityStatus(status, 0, 0))

        controller.setData(data)
        controller.setHasActivityStatus(true)

        verify(mockActivityStatusDrawable).setActivityStatus(status, false)
    }

    @Test
    fun `When view is measured, then image loader is notified about it`() {
        controller.onMeasured()

        verify(mockImageLoader).onViewMeasured()
    }

    @Test
    fun `When view is laid out, then it is forwarded to image view, considering padding`() {
        val startPadding = 11
        val topPadding = 22
        val viewSize = 50
        whenever(mockPersonView.paddingStart).thenReturn(startPadding)
        whenever(mockPersonView.paddingTop).thenReturn(topPadding)

        controller.onSizeChanged(viewSize)
        controller.performLayout()

        verify(mockPersonImageView).layout(startPadding, topPadding, startPadding + viewSize, topPadding + viewSize)
    }

    @Test
    fun `When view is drawn, then image view is notified about it`() {
        val canvas = mock<Canvas>()

        controller.performDraw(canvas)

        verify(mockPersonImageView).draw(canvas)
    }

    @Test
    fun `When view is invalidated, then image view is notified about it`() {
        controller.performInvalidate()

        verify(mockPersonImageView).invalidate()
    }

    @Test
    fun `When view visibility is aggregated, then image loader is notified about it`() {
        controller.onVisibilityAggregated(true)

        verify(mockImageLoader).onVisibilityAggregated(true)
    }

    @Test
    fun `When corner radius is set, then it is set to image view`() {
        controller.performInvalidate()

        verify(mockPersonImageView).invalidate()
    }

    @Test
    fun `When initials color is set, then it is set to image view`() {
        val color = Color.GREEN

        controller.setInitialsColor(color)

        verify(mockPersonImageView).initialsColor = color
    }

}