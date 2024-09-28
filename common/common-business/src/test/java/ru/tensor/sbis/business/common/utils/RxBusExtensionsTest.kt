package ru.tensor.sbis.business.common.utils

import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import io.reactivex.disposables.CompositeDisposable
import org.junit.Test
import ru.tensor.sbis.common.rx.RxBus

class RxBusExtensionsTest {

    private class TestEvent

    private val rxBus = RxBus()
    private val testEvent = TestEvent()
    private val mockAction: (TestEvent) -> Unit = mock()

    @Test
    fun `when subscribed to event, then call action`() {
        @Suppress("RemoveExplicitTypeArguments") rxBus.subscribe<TestEvent>(mockAction)
        rxBus.post(testEvent)

        verify(mockAction).invoke(testEvent)
    }

    @Test
    fun `when subscribed to event without compositeDisposable clear, then call action`() {
        val compositeDisposable = CompositeDisposable()

        @Suppress("RemoveExplicitTypeArguments") rxBus.subscribe<TestEvent>(
            compositeDisposable,
            mockAction
        )
        rxBus.post(testEvent)

        verify(mockAction).invoke(testEvent)
    }

    @Test
    fun `when subscribed to event with compositeDisposable clear, then call action`() {
        val compositeDisposable = CompositeDisposable()

        @Suppress("RemoveExplicitTypeArguments") rxBus.subscribe<TestEvent>(
            compositeDisposable,
            mockAction
        )
        compositeDisposable.clear()
        rxBus.post(testEvent)

        verify(mockAction, never()).invoke(testEvent)
    }
}
