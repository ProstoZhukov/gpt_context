package ru.tensor.sbis.list.view.utils

import androidx.recyclerview.widget.LinearLayoutManager
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.kotlin.never
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.list.view.calback.ListViewListener

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class NeedLoadMoreNotifierTest {
    @Mock
    lateinit var listViewListener: ListViewListener

    @Mock
    lateinit var layoutManager: LinearLayoutManager

    //region loadNext test's
    @Test
    fun `When scroll to end, last visible position more then itemCount-3, and should notify next, then loadNext() invoke`() {
        val itemCount = 10
        val lastVisibleItemPosition = itemCount - 3
        val needLoadMoreNotifier = prepareNeedLoadMoreNotifierWithLastVisibleItem(itemCount, lastVisibleItemPosition)

        needLoadMoreNotifier.shouldNotifyNext(true)
        needLoadMoreNotifier.onScrolled(mock(), 0, 1)
        needLoadMoreNotifier.onScrollStateChanged(mock(), 1)

        verify(listViewListener).loadNext()
    }

    @Test
    fun `When scroll to start, last visible position more then itemCount-6, and should notify next, then loadNext() not invoke`() {
        val itemCount = 10
        val lastVisibleItemPosition = itemCount - 6
        val needLoadMoreNotifier = prepareNeedLoadMoreNotifierWithLastVisibleItem(itemCount, lastVisibleItemPosition)

        needLoadMoreNotifier.shouldNotifyNext(true)
        needLoadMoreNotifier.onScrolled(mock(), 0, -1)
        needLoadMoreNotifier.onScrollStateChanged(mock(), 1)

        verify(listViewListener, never()).loadNext()
    }

    @Test
    fun `When scroll not changed, last visible position more then itemCount-3, and should notify next, then loadNext() not invoke`() {
        val itemCount = 10
        val lastVisibleItemPosition = itemCount - 2
        val needLoadMoreNotifier = prepareNeedLoadMoreNotifierWithLastVisibleItem(itemCount, lastVisibleItemPosition)

        needLoadMoreNotifier.shouldNotifyNext(true)

        verify(listViewListener, never()).loadNext()
    }

    @Test
    fun `When scroll to end multiple times, last visible position more then itemCount-3, and should notify, then loadNext() invoke once`() {
        val itemCount = 10
        val lastVisibleItemPosition = itemCount - 2
        val needLoadMoreNotifier = prepareNeedLoadMoreNotifierWithLastVisibleItem(itemCount, lastVisibleItemPosition)

        needLoadMoreNotifier.shouldNotifyNext(true)
        needLoadMoreNotifier.onScrolled(mock(), 0, 1)
        needLoadMoreNotifier.onScrollStateChanged(mock(), 1)
        needLoadMoreNotifier.onScrolled(mock(), 0, 10)
        needLoadMoreNotifier.onScrollStateChanged(mock(), 1)

        verify(listViewListener).loadNext()
    }
    //endregion

    //region loadPrevious test's
    @Test
    fun `When scroll to start, first visible position less then 3, and should notify previous, then loadPrevious() invoke`() {
        val itemCount = 5
        val lastVisibleItemPosition = 0
        val needLoadMoreNotifier = prepareNeedLoadMoreNotifierWithFirstVisibleItem(itemCount, lastVisibleItemPosition)

        needLoadMoreNotifier.shouldNotifyPrevious(true)
        needLoadMoreNotifier.onScrolled(mock(), 0, -1)
        needLoadMoreNotifier.onScrollStateChanged(mock(), 1)

        verify(listViewListener).loadPrevious()
    }

    @Test
    fun `When scroll to end, first visible position less then 3, and should notify previous, then loadPrevious() not invoke`() {
        val itemCount = 5
        val firstVisibleItemPosition = 2
        val needLoadMoreNotifier = prepareNeedLoadMoreNotifierWithFirstVisibleItem(itemCount, firstVisibleItemPosition)

        needLoadMoreNotifier.shouldNotifyPrevious(true)
        needLoadMoreNotifier.onScrolled(mock(), 0, 1)
        needLoadMoreNotifier.onScrollStateChanged(mock(), 1)

        verify(listViewListener, never()).loadPrevious()
    }

    @Test
    fun `When scroll not changed, first visible position less then 3, and should notify previous, then loadPrevious() not invoke`() {
        val itemCount = 5
        val firstVisibleItemPosition = 2
        val needLoadMoreNotifier = prepareNeedLoadMoreNotifierWithFirstVisibleItem(itemCount, firstVisibleItemPosition)

        needLoadMoreNotifier.shouldNotifyPrevious(true)

        verify(listViewListener, never()).loadPrevious()
    }

    @Test
    fun `When scroll to start multiple times, first visible position less then 3, and should notify previous, then loadPrevious() invoke once`() {
        val itemCount = 5
        val firstVisibleItemPosition = 2
        val needLoadMoreNotifier = prepareNeedLoadMoreNotifierWithFirstVisibleItem(itemCount, firstVisibleItemPosition)

        needLoadMoreNotifier.shouldNotifyPrevious(true)
        needLoadMoreNotifier.onScrolled(mock(), 0, -1)
        needLoadMoreNotifier.onScrollStateChanged(mock(), 1)
        needLoadMoreNotifier.onScrolled(mock(), 0, -10)
        needLoadMoreNotifier.onScrollStateChanged(mock(), 1)

        verify(listViewListener).loadPrevious()
    }
    //endregion

    private fun prepareNeedLoadMoreNotifierWithLastVisibleItem(
        itemCount: Int,
        visibleItemPosition: Int
    ): NeedLoadMoreNotifier {
        whenever(layoutManager.itemCount).thenReturn(itemCount)
        whenever(layoutManager.findLastVisibleItemPosition()).thenReturn(visibleItemPosition)
        return NeedLoadMoreNotifier(listViewListener, layoutManager)
    }

    private fun prepareNeedLoadMoreNotifierWithFirstVisibleItem(
        itemCount: Int,
        visibleItemPosition: Int
    ): NeedLoadMoreNotifier {
        whenever(layoutManager.itemCount).thenReturn(itemCount)
        whenever(layoutManager.findFirstVisibleItemPosition()).thenReturn(visibleItemPosition)
        return NeedLoadMoreNotifier(listViewListener, layoutManager)
    }
}