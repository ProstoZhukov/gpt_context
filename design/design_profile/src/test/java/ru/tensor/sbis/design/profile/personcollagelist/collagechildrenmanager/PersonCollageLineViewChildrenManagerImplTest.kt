package ru.tensor.sbis.design.profile.personcollagelist.collagechildrenmanager

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.view.ViewGroup
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.profile.imageview.PersonImageView
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile.personcollagelist.util.PersonCollageLineViewPool
import ru.tensor.sbis.design.profile.util.BitmapResult
import java.util.UUID

/**
 * @author us.bessonov
 */
@Config(manifest = Config.NONE, sdk = [28])
@RunWith(RobolectricTestRunner::class)
class PersonCollageLineViewChildrenManagerImplTest {

    private val context: Context = Robolectric.buildActivity(Activity::class.java).setup().get()

    @get:Rule
    val rxRule = TrampolineSchedulerRule()

    private val childrenManager = PersonCollageLineViewChildrenManagerImpl(
        getBitmapsObservable = { _, _, _ -> bitmapsObservable },
        setBitmapFromCache = { _, _, _ -> false }
    )

    private val mockCollageView: ViewGroup = mock()

    private var mockViewPool: PersonCollageLineViewPool = mock()

    private var mockItemView0: PersonImageView = mock()

    private var mockItemView1: PersonImageView = mock()

    private var mockItemView2: PersonImageView = mock()

    private var mockResources: Resources = mock()

    private lateinit var bitmapsObservable: Observable<BitmapResult>

    @Before
    fun setUp() {
        context.theme.applyStyle(R.style.AppGlobalTheme, false)
        bitmapsObservable = Observable.empty()
        whenever(mockCollageView.context).thenReturn(context)

        childrenManager.setCollageView(mockCollageView)
        childrenManager.setViewPool(mockViewPool)
    }

    @Test
    fun `When data is set, and there are more views than needed, then unused views are removed and added to view pool, and the data is applied as expected`() {
        val data = (0 until 3).map { PersonData(UUID.randomUUID(), "Url $it") }
        whenever(mockViewPool.get("Url 0")).thenReturn(mockItemView0)
        whenever(mockViewPool.get("Url 1")).thenReturn(mockItemView1)
        whenever(mockViewPool.get("Url 2")).thenReturn(mockItemView2)

        childrenManager.updateChildren(3, 0, data)
        childrenManager.updateChildren(2, 0, data.take(2))

        verify(mockViewPool).recycle(mockItemView2)
        verify(mockItemView0, times(2)).setData(data[0])
        verify(mockItemView1, times(2)).setData(data[1])
        verify(mockItemView2).setData(data[2])
    }

    @Test
    fun `When data is set, and there are hidden items, then counter is added`() {
        val itemSize = 42
        val hiddenCount = 5
        val data = (0 until 2).map { PersonData(UUID.randomUUID(), "Url $it") }
        whenever(mockCollageView.resources).thenReturn(mockResources)
        whenever(mockViewPool.get("Url 0")).thenReturn(mockItemView0)
        whenever(mockViewPool.get("Url 1")).thenReturn(mockItemView1)

        childrenManager.updateItemSize(itemSize, 6, 24f)
        childrenManager.updateChildren(2, hiddenCount, data)

        verify(mockItemView0).resetCounter(0)
        verify(mockItemView1).resetCounter(6)
    }

    @Test
    fun `When bitmap is loaded, then it is applied to item and view is invalidated`() {
        val data = (0 until 2).map { PersonData(UUID.randomUUID(), "Url $it") }
        val bitmap0 = mock<Bitmap>()
        val bitmap1 = mock<Bitmap>()
        whenever(mockViewPool.get("Url 0")).thenReturn(mockItemView0)
        whenever(mockViewPool.get("Url 1")).thenReturn(mockItemView1)
        whenever(mockItemView0.setBitmap(bitmap0)).thenReturn(true)
        whenever(mockItemView1.setBitmap(bitmap1)).thenReturn(true)
        bitmapsObservable = Observable.just(BitmapResult(bitmap0, 0), BitmapResult(bitmap1, 1))

        childrenManager.updateChildren(2, 0, data)

        verify(mockItemView0).setBitmap(bitmap0)
        verify(mockItemView1).setBitmap(bitmap1)
        verify(mockCollageView, times(2)).invalidate()
    }
}