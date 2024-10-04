package ru.tensor.sbis.design.profile.person
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.design.profile.person.ActivityStatusBitmapProvider.ActivityStatusBitmaps
import ru.tensor.sbis.person_decl.profile.model.ActivityStatus
import ru.tensor.sbis.person_decl.profile.model.ActivityStatus.ONLINE_WORK

@RunWith(AndroidJUnit4::class)
class ActivityStatusDrawableTest {

    private lateinit var context: Context
    private lateinit var attrs: AttributeSet
    private lateinit var styleHolder: ActivityStatusStyleHolder
    private lateinit var bitmaps: ActivityStatusBitmaps
    private lateinit var drawable: ActivityStatusDrawable

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        attrs = mockk(relaxed = true)
        styleHolder = mockk(relaxed = true)
        bitmaps = mockk(relaxed = true)

        every { styleHolder.statusSizeMedium } returns 50
        every { bitmaps.onlineWorkBitmap } returns mockk()
        every { bitmaps.offlineWorkBitmap } returns mockk()
        every { bitmaps.onlineHomeBitmap } returns mockk()
        every { bitmaps.offlineHomeBitmap } returns mockk()

        mockkConstructor(ActivityStatusBitmaps::class)
        every { anyConstructed<ActivityStatusBitmaps>().onlineWorkBitmap } returns bitmaps.onlineWorkBitmap
        every { anyConstructed<ActivityStatusBitmaps>().offlineWorkBitmap } returns bitmaps.offlineWorkBitmap
        every { anyConstructed<ActivityStatusBitmaps>().onlineHomeBitmap } returns bitmaps.onlineHomeBitmap
        every { anyConstructed<ActivityStatusBitmaps>().offlineHomeBitmap } returns bitmaps.offlineHomeBitmap

        drawable = spyk(ActivityStatusDrawable(context, attrs, styleHolder))
    }

    @Test
    fun `SetActivityStatus_WithOnlineWork_SetsOnlineWorkBitmap`() {
        val onlineWorkBitmap = bitmaps.onlineWorkBitmap

        // act
        drawable.setActivityStatus(ONLINE_WORK, false)

        // verify
        assertEquals(onlineWorkBitmap, drawable.statusImage)
    }

    @Test
    fun `SetActivityStatus_WithOfflineWork_SetsOfflineWorkBitmap`() {
        val offlineWorkBitmap = bitmaps.offlineWorkBitmap

        // act
        drawable.setActivityStatus(ActivityStatus.OFFLINE_WORK, false)

        // verify
        assertEquals(offlineWorkBitmap, drawable.statusImage)
    }

    @Test
    fun `SetActivityStatus_WithOnlineHome_SetsOnlineHomeBitmap`() {
        val onlineHomeBitmap = bitmaps.onlineHomeBitmap

        // act
        drawable.setActivityStatus(ActivityStatus.ONLINE_HOME, false)

        // verify
        assertEquals(onlineHomeBitmap, drawable.statusImage)
    }

    @Test
    fun `SetActivityStatus_WithOfflineHomeAndDisplayTrue_SetsOfflineHomeBitmap`() {
        val offlineHomeBitmap = bitmaps.offlineHomeBitmap

        // act
        drawable.setActivityStatus(ActivityStatus.OFFLINE_HOME, true)

        // verify
        assertEquals(offlineHomeBitmap, drawable.statusImage)
    }

    @Test
    fun `SetActivityStatus_WithOfflineHomeAndDisplayFalse_DoesNotSetOfflineHomeBitmap`() {
        // act
        drawable.setActivityStatus(ActivityStatus.OFFLINE_HOME, false)

        // verify
        assertNull(drawable.statusImage)
    }

    @Test
    fun `SetActivityStatus_WithUnknownStatus_SetsStatusImageToNull`() {
        // act
        drawable.setActivityStatus(ActivityStatus.UNKNOWN, false)

        // verify
        assertNull(drawable.statusImage)
    }

    @Test
    fun `Draw_WithNotVisible_DoesNotDrawBitmap`() {
        val canvas = mockk<Canvas>(relaxed = true)

        drawable.setVisible(false, false)

        // act
        drawable.draw(canvas)

        // verify
        verify(exactly = 0) { canvas.drawBitmap(any<Bitmap>(), null, any<Rect>(), any()) }
    }

    @Test
    fun `Draw_WithNullStatusImage_DoesNotDrawBitmap`() {
        val canvas = mockk<Canvas>(relaxed = true)

        drawable.setVisible(true, false)
        drawable.statusImage = null

        // act
        drawable.draw(canvas)

        // verify
        verify(exactly = 0) { canvas.drawBitmap(any<Bitmap>(), null, any<Rect>(), any()) }
    }

    @Test
    fun `SetAlpha_UpdatesPaintAlpha`() {
        val alpha = 128

        // act
        drawable.setAlpha(alpha)

        // verify
        assertEquals(alpha, drawable.paint.alpha)
    }

    @Test
    fun `SetColorFilter_UpdatesPaintColorFilter`() {
        val colorFilter = mockk<ColorFilter>()

        // act
        drawable.setColorFilter(colorFilter)

        // verify
        assertEquals(colorFilter, drawable.paint.colorFilter)
    }

    @Test
    fun `GetOpacity_ReturnsTranslucent`() {
        // act
        val opacity = drawable.opacity

        // verify
        assertEquals(PixelFormat.TRANSLUCENT, opacity)
    }

    @Test
    fun `GetIntrinsicWidth_ReturnsSize`() {
        // act
        val width = drawable.intrinsicWidth

        // verify
        assertEquals(50, width)
    }

    @Test
    fun `GetIntrinsicHeight_ReturnsSize`() {
        // act
        val height = drawable.intrinsicHeight

        // verify
        assertEquals(50, height)
    }
}