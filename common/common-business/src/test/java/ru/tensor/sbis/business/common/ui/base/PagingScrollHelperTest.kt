package ru.tensor.sbis.business.common.ui.base

import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import ru.tensor.sbis.business.common.ui.base.PagingScrollHelper.ScrollInitiator
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule

@Suppress("RedundantVisibilityModifier", "UsePropertyAccessSyntax")
class PagingScrollHelperTest {

    @get:Rule
    public var rule = TrampolineSchedulerRule()

    private val mockLifecycle: Lifecycle = mock()
    private val mockRecyclerView: RecyclerView = mock()

    private val mockScrollingInitiator: ScrollInitiator = mock {
        // при вызове provideInitiator() возвращаем лямбду, которая возвращает mocked RecyclerView:(Unit) -> RecyclerView?
        on { provideInitiator() } doReturn { mockRecyclerView }
        on { lifecycle } doReturn mockLifecycle
    }

    @Test
    fun `starts lifecycle listen on init`() {
        PagingScrollHelper(mockScrollingInitiator)

        verify(mockLifecycle).addObserver(any())
    }

    @Test
    fun `sets scroll listener to RecyclerView on subscribe`() {
        val helper = PagingScrollHelper(mockScrollingInitiator).apply {
            provideInitiator = { mockRecyclerView }
        }

        helper.observePaging().subscribe()

        assertTrue(helper.isAssignedScrollListening)
        verify(mockRecyclerView).addOnScrollListener(any())
    }

    @Test
    fun `block receiver by id`() {
        val id = "receiver id"
        val helper = PagingScrollHelper(mockScrollingInitiator).apply {
            receiverList.add(PagingConsumer(id, processScroll = true))
            receiverList.add(PagingConsumer("receiver id 222", processScroll = true))
        }

        helper.block(id)

        assertFalse(helper.receiverList[0].processScroll)
        assertTrue(helper.receiverList[1].processScroll)
    }

    @Test
    fun `unblock receiver by id`() {
        val id = "receiver id"
        val helper = PagingScrollHelper(mockScrollingInitiator).apply {
            receiverList.add(PagingConsumer(id, processScroll = false))
            receiverList.add(PagingConsumer("receiver id 222", processScroll = false))
        }

        helper.relieve(id)

        assertTrue(helper.receiverList[0].processScroll)
        assertFalse(helper.receiverList[1].processScroll)
    }

    @Test
    fun `adding on subscription and removing on dispose`() {
        val id1 = "id1"
        val helper = PagingScrollHelper(mockScrollingInitiator)

        val observer = helper.observePaging(id1).subscribe()

        assertEquals(1, helper.receiverList.size)
        assertEquals(id1, helper.receiverList[0].id)

        observer.dispose()

        assertTrue(helper.receiverList.isEmpty())
    }

    @Test
    fun `observe active receiver`() {
        val id = "id1"
        val helper = PagingScrollHelper(mockScrollingInitiator)

        var wasCalled = false
        helper.observePaging(id).subscribe {
            wasCalled = true
        }
        helper.pagingChannel.onNext(Unit)

        assertTrue(wasCalled)
    }

    @Test
    fun `don't observe not active receiver`() {
        val id = "id1"
        val helper = PagingScrollHelper(mockScrollingInitiator)
        var wasCalled = false

        helper.observePaging(id).subscribe {
            wasCalled = true
        }
        helper.block(id)
        helper.pagingChannel.onNext(Unit)

        assertFalse(wasCalled)
    }
}
