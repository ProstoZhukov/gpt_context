package ru.tensor.sbis.design.profile.personcollage.controller

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.view.ViewGroup
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.tensor.sbis.design.profile.R
import ru.tensor.sbis.design.profile.imageview.PersonImageView
import ru.tensor.sbis.design.profile.mockPhotoSize
import ru.tensor.sbis.design.profile.personcollage.CollageGridView
import ru.tensor.sbis.design.profile_decl.person.CompanyData
import ru.tensor.sbis.design.profile_decl.person.GroupData
import ru.tensor.sbis.design.profile_decl.person.ImageData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.utils.image_loading.ImageUrl
import ru.tensor.sbis.design.utils.image_loading.RawBitmap
import ru.tensor.sbis.design.utils.image_loading.ViewImageLoader

/**
 * @author us.bessonov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class CollageGridViewControllerImplTest {

    @Mock
    private lateinit var mockView: CollageGridView

    @Mock
    private lateinit var mockImageView: PersonImageView

    @Mock
    private lateinit var mockImageLoader: ViewImageLoader

    @Mock
    private lateinit var mockResources: Resources

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockPlaceholderBitmapProvider: (Resources, Int) -> Bitmap?

    private lateinit var controller: CollageGridViewController

    @Before
    fun setUp() {
        whenever(mockView.resources).thenReturn(mockResources)
        whenever(mockView.context).thenReturn(mockContext)
        controller = CollageGridViewControllerImpl(
            Color.WHITE,
            mock(),
            { mockImageLoader },
            { mockImageView },
            mockPlaceholderBitmapProvider
        ).apply {
            init(mockView)
        }
    }

    @Test
    fun `When data with no photo and with custom placeholder is set, then custom placeholder is displayed`() {
        val mockPlaceholder = mock<Bitmap>()
        val placeholderId = 33
        whenever(mockPlaceholderBitmapProvider.invoke(mockResources, placeholderId)).thenReturn(mockPlaceholder)

        controller.setDataList(listOf(ImageData(null, placeholderId)))

        verify(mockImageView).setBitmap(mockPlaceholder)
        verify(mockView).invalidate()
    }

    @Test
    fun `When common custom placeholder is set, and there are no photos, then custom placeholder is displayed`() {
        val mockPlaceholder = mock<Bitmap>()
        val placeholderId = 33
        whenever(mockPlaceholderBitmapProvider.invoke(mockResources, placeholderId)).thenReturn(mockPlaceholder)

        controller.setCustomPlaceholder(placeholderId)

        verify(mockImageView).setBitmap(mockPlaceholder)
        verify(mockView).invalidate()
    }

    @Test
    fun `When single data with no photo is set, then expected placeholder is displayed`() {
        val mockPlaceholder = mock<Bitmap>()
        whenever(
            mockPlaceholderBitmapProvider.invoke(
                mockResources,
                R.drawable.design_profile_person_placeholder
            )
        ).thenReturn(mockPlaceholder)

        controller.setDataList(listOf(PersonData()))

        verify(mockImageView).setBitmap(mockPlaceholder)
        verify(mockView).invalidate()
    }

    @Test
    fun `When data list contains only 2 items without photo, then proper placeholder is displayed`() {
        val mockPlaceholder = mock<Bitmap>()
        whenever(
            mockPlaceholderBitmapProvider.invoke(
                mockResources,
                R.drawable.design_profile_two_persons_placeholder
            )
        ).thenReturn(mockPlaceholder)

        controller.setDataList(listOf(PersonData(), GroupData(null)))

        verify(mockImageView).setBitmap(mockPlaceholder)
        verify(mockView).invalidate()
    }

    @Test
    fun `When data list contains only 3 items without photo, then proper placeholder is displayed`() {
        val mockPlaceholder = mock<Bitmap>()
        whenever(
            mockPlaceholderBitmapProvider.invoke(
                mockResources,
                R.drawable.design_profile_three_persons_placeholder
            )
        ).thenReturn(mockPlaceholder)

        controller.setDataList(listOf(PersonData(), GroupData(null), CompanyData(null)))

        verify(mockImageView).setBitmap(mockPlaceholder)
        verify(mockView).invalidate()
    }

    @Test
    fun `When list consists of single item with photo, then single photo is displayed in collage`() {
        val photoUrl = "Item 0 url"
        val items = listOf(
            PersonData(photoUrl = photoUrl)
        )
        val size = 42
        val mockLayoutParams = mock<ViewGroup.LayoutParams>()
            .apply { width = size }
        whenever(mockView.layoutParams).thenReturn(mockLayoutParams)

        controller.setDataList(items)

        verify(mockImageLoader).setImages(listOf(ImageUrl(photoUrl)))
        verify(mockView).requestLayout()
    }

    @Test
    fun `When list consists of 2 items with photo, then 2 photos are displayed in collage`() {
        val photoUrls = listOf("Item 0 url", "Item 1 url")
        val items = photoUrls.map {
            PersonData(photoUrl = it)
        }
        val size = 42
        val mockLayoutParams = mock<ViewGroup.LayoutParams>()
            .apply { width = size }
        whenever(mockView.layoutParams).thenReturn(mockLayoutParams)

        controller.setDataList(items)

        verify(mockImageLoader).setImages(photoUrls.map(::ImageUrl))
        verify(mockView).requestLayout()
    }

    @Test
    fun `When list contains at least 4 items with photo, then 4 photos are displayed in collage`() {
        val mockPlaceholder = mock<Bitmap>()
        whenever(
            mockPlaceholderBitmapProvider.invoke(
                mockResources,
                R.drawable.design_profile_person_placeholder
            )
        ).thenReturn(mockPlaceholder)
        val photoUrls = listOf(
            "Item 0 url",
            "Item 1 url",
            String(),
            "Item 3 url",
            "Item 4 url"
        )
        val items = photoUrls.map {
            PersonData(photoUrl = it.takeUnless { it.isEmpty() })
        }
        val size = 42
        val mockLayoutParams = mock<ViewGroup.LayoutParams>()
            .apply { width = size }
        whenever(mockView.layoutParams).thenReturn(mockLayoutParams)

        controller.setDataList(items)

        verify(mockImageLoader).setImages(
            listOf(
                ImageUrl(photoUrls[0]),
                ImageUrl(photoUrls[1]),
                ImageUrl(photoUrls[3]),
                ImageUrl(photoUrls[4]),
                RawBitmap(mockPlaceholder)
            )
        )
        verify(mockView).requestLayout()
    }

    @Test
    fun `When list contains 2 items with photo and more than one item without photo, then 2 photos and only one placeholder are displayed in collage`() {
        val mockPlaceholder = mock<Bitmap>()
        whenever(
            mockPlaceholderBitmapProvider.invoke(
                mockResources,
                R.drawable.design_profile_person_placeholder
            )
        ).thenReturn(mockPlaceholder)
        val photoUrls = listOf(
            String(),
            String(),
            "Item 2 url",
            String(),
            "Item 4 url",
            String()
        )
        val items = photoUrls.map {
            PersonData(photoUrl = it.takeUnless { it.isEmpty() })
        }
        val size = 42
        val mockLayoutParams = mock<ViewGroup.LayoutParams>()
            .apply { width = size }
        whenever(mockView.layoutParams).thenReturn(mockLayoutParams)

        controller.setDataList(items)

        verify(mockImageLoader).setImages(
            listOf(
                ImageUrl(photoUrls[2]),
                ImageUrl(photoUrls[4]),
                RawBitmap(mockPlaceholder)
            )
        )
        verify(mockView).requestLayout()
    }

    @Test
    fun `When new photo size is set, then layout is updated accordingly`() {
        val sizePx = 42
        val mockPhotoSize = mockPhotoSize(mockContext, sizePx)
        val mockLayoutParams = mock<ViewGroup.LayoutParams>()
        whenever(mockView.layoutParams).thenReturn(mockLayoutParams)

        controller.setSize(mockPhotoSize)

        assertEquals(mockLayoutParams.width, sizePx)
        assertEquals(mockLayoutParams.height, sizePx)
        verify(mockView).requestLayout()
        verify(mockImageView).setPhotoSize(sizePx)
    }

    @Test
    fun `When new size that requires bigger preview is set, then data is set again`() {
        val sizeS = 24
        val mockPhotoSizeS = mockPhotoSize(mockContext, sizeS) {
            on { ordinal } doReturn 0
        }
        val sizeL = 42
        val mockPhotoSizeL = mockPhotoSize(mockContext, sizeL) {
            on { ordinal } doReturn 1
        }
        val mockLayoutParams = mock<ViewGroup.LayoutParams>()
        val personData = PersonData(photoUrl = "https://service/previewer/r/%d/%d/collage")
        whenever(mockView.layoutParams).thenReturn(mockLayoutParams)

        controller.setSize(mockPhotoSizeS)
        controller.setDataList(listOf(personData))
        controller.setSize(mockPhotoSizeL)

        verify(mockImageLoader).setImages(listOf(ImageUrl("https://service/previewer/r/24/24/collage")))
        verify(mockImageLoader).setImages(listOf(ImageUrl("https://service/previewer/r/42/42/collage")))
    }

}