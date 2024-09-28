package ru.tensor.sbis.list.view.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Test
import ru.tensor.sbis.list.view.calback.ListViewListener

internal class NeedLoadMoreNotifierCallbackHandlerTest {

    private val callback = TestListView()
    private val mockCreateNeedLoadMoreNotifier = mock<(ListViewListener) -> NeedLoadMoreNotifier>()
    private val mockNeedLoadMoreNotifier0 = mock<NeedLoadMoreNotifier>()
    private val mockNeedLoadMoreNotifier1 = mock<NeedLoadMoreNotifier>()
    private val mockRecyclerView = mock<RecyclerView>()
    private val mockLayoutManager = mock<LinearLayoutManager>()
    private val handler = NeedLoadMoreNotifierCallbackHandler(mockLayoutManager, mockCreateNeedLoadMoreNotifier)

    @Test
    fun handle() {
        whenever(mockCreateNeedLoadMoreNotifier(callback)).doReturn(mockNeedLoadMoreNotifier0)

        handler.handle(mockRecyclerView, callback)

        verify(mockRecyclerView).addOnScrollListener(mockNeedLoadMoreNotifier0)
    }

    @Test
    fun shouldNotifyNext() {
        whenever(mockCreateNeedLoadMoreNotifier(callback)).doReturn(mockNeedLoadMoreNotifier0)
        handler.handle(mockRecyclerView, callback)

        handler.shouldNotifyNext(true)

        verify(mockNeedLoadMoreNotifier0).shouldNotifyNext(true)
    }

    @Test
    fun shouldNotifyPrevious() {
        whenever(mockCreateNeedLoadMoreNotifier(callback)).doReturn(mockNeedLoadMoreNotifier0)
        handler.handle(mockRecyclerView, callback)

        handler.shouldNotifyPrevious(true)

        verify(mockNeedLoadMoreNotifier0).shouldNotifyPrevious(true)
    }

    @Test
    fun `Given one callback already handle, when handle new one, then remove previous first`() {
        whenever(mockCreateNeedLoadMoreNotifier(callback))
            .doReturn(mockNeedLoadMoreNotifier0)
            .thenReturn(mockNeedLoadMoreNotifier1)
        handler.handle(mockRecyclerView, callback)
        clearInvocations(mockRecyclerView)
        //act
        handler.handle(mockRecyclerView, callback)
        //verify
        val ordered = inOrder(mockRecyclerView)
        ordered.verify(mockRecyclerView).removeOnScrollListener(mockNeedLoadMoreNotifier0)
        ordered.verify(mockRecyclerView).addOnScrollListener(mockNeedLoadMoreNotifier1)
    }

    @Test
    fun default() {
        NeedLoadMoreNotifierCallbackHandler(mock())
    }
}

class TestListView : ListViewListener {

    override fun loadPrevious() {}

    override fun loadNext() {}
}