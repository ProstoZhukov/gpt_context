package ru.tensor.sbis.list.view

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import io.reactivex.subjects.PublishSubject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.list.R
import ru.tensor.sbis.list.utils.BaseThemedActivity
import ru.tensor.sbis.list.view.adapter.SbisAdapter
import ru.tensor.sbis.list.view.background.ColorProvider
import ru.tensor.sbis.list.view.calback.ItemMoveCallback
import ru.tensor.sbis.list.view.calback.ListViewListener
import ru.tensor.sbis.list.view.decorator.DecoratorHolder
import ru.tensor.sbis.list.view.decorator.stiky_header.StickyHeaderInterface
import ru.tensor.sbis.list.view.utils.BottomLoadMoreProgressHelper
import ru.tensor.sbis.list.view.utils.ItemTouchHelperAttacher
import ru.tensor.sbis.list.view.utils.NeedLoadMoreNotifierCallbackHandler
import ru.tensor.sbis.list.view.utils.Plain
import ru.tensor.sbis.list.view.utils.ScrollerToPosition
import ru.tensor.sbis.list.view.utils.TopLoadMoreProgressHelper
import ru.tensor.sbis.list.view.utils.layout_manager.SbisGridLayoutManager
import ru.tensor.sbis.design.R as RDesign

/**
 * http://axure.tensor.ru/MobileStandart8/#p=%D1%82%D0%B0%D0%B1%D0%BB%D0%B8%D1%87%D0%BD%D0%BE%D0%B5_%D0%BF%D1%80%D0%B5%D0%B4%D1%81%D1%82%D0%B0%D0%B2%D0%BB%D0%B5%D0%BD%D0%B8%D0%B5__%D0%B2%D0%B5%D1%80%D1%81%D0%B8%D1%8F_02_&g=1
 *
 */
@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
@Ignore
class SbisListTest {

    @get:Rule
    val rxSchedulerRule = TrampolineSchedulerRule()

    private val activityController = Robolectric.buildActivity(BaseThemedActivity::class.java).setup()
    private val activity = activityController.get()
    private val colorWhite10 = RDesign.color.palette_color_white1
    private val mockSectionsHolder = mock<ListDataHolder> {
        on { getBackgroundResId(any()) } doReturn colorWhite10
    }
    private val mockDecoratorHolder = mock<DecoratorHolder>()
    private val mockItemTouchHelperAttacher = mock<ItemTouchHelperAttacher>()
    private val mockAdapter = mock<SbisAdapter>()
    private val mockNeedLoadMoreNotifierCallbackHandler = mock<NeedLoadMoreNotifierCallbackHandler>()
    private val mockBottomLoadMoreProgressHelper = mock<BottomLoadMoreProgressHelper>()
    private val mockTopLoadMoreProgressHelper = mock<TopLoadMoreProgressHelper>()
    private val lifecycleOwner = mock<LifecycleOwner>{
        on { lifecycle } doReturn mock()
    }
    private val mockScrollerToPosition = mock<ScrollerToPosition>()
    private val mockColorProvider = mock<ColorProvider>()
    private val layoutManager = mock<SbisGridLayoutManager>()
    private lateinit var sbisList: SbisList

    @Before
    fun init() {
        sbisList = createSbisList()
    }

    @Test
    fun `Initial state`() {
        verify(mockDecoratorHolder).addDecorators(
            eq(sbisList),
            eq(layoutManager),
            eq(mockSectionsHolder),
            eq(sbisList.adapter as StickyHeaderInterface),
            eq(sbisList.resources.getDimensionPixelSize(R.dimen.list_divider_space_size)),
            eq(mockColorProvider),
            any()
        )
        assertEquals(
            mockAdapter,
            sbisList.adapter
        )
        assertEquals(
            layoutManager,
            sbisList.layoutManager
        )
        assertEquals(
            0,
            sbisList.itemAnimator!!.changeDuration
        )
        verify(mockItemTouchHelperAttacher).attach(sbisList)
        verify(mockAdapter).registerAdapterDataObserver(mockScrollerToPosition)
        verify(mockAdapter).registerSelectionObserver(sbisList)
        verify(mockBottomLoadMoreProgressHelper).attach(
            sbisList,
            layoutManager,
            mockAdapter,
            mockDecoratorHolder
        )
        verify(mockTopLoadMoreProgressHelper).setAdapter(mockAdapter)
        verify(mockScrollerToPosition).setListAndLayoutManager(sbisList, layoutManager)
        verify(layoutManager).isMeasurementCacheEnabled = true
    }

    @Test
    fun default() {
        val recyclerView = SbisList(activity)

        assertTrue(recyclerView.adapter is SbisAdapter)
        assertTrue(sbisList.layoutManager is LinearLayoutManager)
    }

    @Test
    fun setLoadMoreCallback() {
        //act
        val loadMoreCallback = mock<ListViewListener>()
        sbisList.setLoadMoreCallback(loadMoreCallback)
        //verify
        verify(mockNeedLoadMoreNotifierCallbackHandler).handle(sbisList, loadMoreCallback)
    }

    @Test
    fun fabPaddingTrue() {
        sbisList.fabPadding(true)

        verify(mockBottomLoadMoreProgressHelper).fabPadding(true)
    }

    @Test
    fun fabPaddingFalse() {
        sbisList.fabPadding(false)

        verify(mockBottomLoadMoreProgressHelper).fabPadding(false)
    }


    @Test
    fun showLoadNextProgress() {
        sbisList.loadNextProgressIsVisible(true)

        verify(mockBottomLoadMoreProgressHelper).setShowProgress(true)
    }

    @Test
    fun hideLoadNextProgress() {
        sbisList.loadNextProgressIsVisible(false)

        verify(mockBottomLoadMoreProgressHelper).setShowProgress(false)
    }

    @Test
    fun `Release bottom load more disposable`() {
        val disposable = PublishSubject.create<Unit>().subscribe()
        whenever(
            mockBottomLoadMoreProgressHelper.attach(
                sbisList,
                layoutManager,
                mockAdapter,
                mockDecoratorHolder
            )
        ).doReturn(disposable)

        assertFalse(disposable.isDisposed)

        activityController.destroy()
        assertFalse(disposable.isDisposed)
    }

    @Test
    fun showLoadPreviousProgress() {
        sbisList.loadPreviousProgressIsVisible(true)

        verify(mockTopLoadMoreProgressHelper).hasLoadMore(true)
    }

    @Test
    fun hideLoadPreviousProgress() {
        sbisList.loadPreviousProgressIsVisible(false)

        verify(mockTopLoadMoreProgressHelper).hasLoadMore(false)
    }

    @Test
    fun cleanSelection() {
        sbisList.cleanSelection()

        verify(mockDecoratorHolder).cleanSelection()
    }

    @Test
    fun highlightSelection() {
        sbisList.highlightSelection()

        verify(mockAdapter).highlightSelection = true
    }

    @Test
    fun highlightItem() {
        val position = 12312
        sbisList.highlightItem(position)

        verify(mockAdapter).notifyItemChanged(position)
        verify(mockDecoratorHolder).highlightItem(position)
    }

    @Test
    fun moveToAddedFalse() {
        sbisList.setShouldMoveToAdded(false)

        verify(mockScrollerToPosition).moveToAdded = false
    }

    @Test
    fun moveToAddedTrue() {
        sbisList.setShouldMoveToAdded(true)

        verify(mockScrollerToPosition).moveToAdded = true
    }

    @Test
    fun itemMoveCallback() {
        val mock = mock<ItemMoveCallback>()

        sbisList.setItemMoveCallback(mock)

        verify(mockItemTouchHelperAttacher).setItemMoveCallback(mock)
    }

    @Test
    fun `Measurement cache enabled if single span`() {
        whenever(mockSectionsHolder.hasCollapsibleItems()).doReturn(true)
        whenever(layoutManager.spanCount).doReturn(1)
        clearInvocations(layoutManager)
        sbisList.setListData(Plain())

        verify(layoutManager).isMeasurementCacheEnabled = true
    }

    @Test
    fun `Measurement cache disabled if multiple spans`() {
        whenever(mockSectionsHolder.hasCollapsibleItems()).doReturn(true)
        whenever(layoutManager.spanCount).doReturn(2)
        clearInvocations(layoutManager)
        sbisList.setListData(Plain())

        verify(layoutManager).isMeasurementCacheEnabled = false
    }

    private fun createSbisList() = SbisList(
        activity,
        listDataHolder = mockSectionsHolder,
        colorProvider = mockColorProvider,
        adapter = mockAdapter,
        decoratorHolder = mockDecoratorHolder,
        bottomLoadMoreProgressHelper = mockBottomLoadMoreProgressHelper,
        topLoadMoreProgressHelper = mockTopLoadMoreProgressHelper,
        layoutManager = layoutManager,
        needLoadMoreNotifierCallbackHandler = mockNeedLoadMoreNotifierCallbackHandler,
        helperAttacher = mockItemTouchHelperAttacher,
        scrollerToPosition = mockScrollerToPosition,
    ).apply {
        setTag(androidx.lifecycle.runtime.R.id.view_tree_lifecycle_owner, lifecycleOwner)
        activity.window.decorView.findViewById<ViewGroup>(android.R.id.content).addView(
            this,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
}