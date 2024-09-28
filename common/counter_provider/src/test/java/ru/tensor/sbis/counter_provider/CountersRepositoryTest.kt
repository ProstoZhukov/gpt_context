package ru.tensor.sbis.counter_provider

import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.generated.BnpCounter
import ru.tensor.sbis.common.generated.BnpCountersController
import ru.tensor.sbis.common.generated.DataRefreshedBnpCountersControllerEvent
import ru.tensor.sbis.platform.generated.Subscription

/**
 * @author mb.kruglova
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class CountersRepositoryTest {

    @Mock
    private lateinit var controller: BnpCountersController

    @Mock
    private lateinit var subscriptionEvent: DataRefreshedBnpCountersControllerEvent

    @Mock
    private lateinit var subscription: Subscription

    private lateinit var repository: CountersRepository

    private val cachedCounters = ArrayList<BnpCounter>(3).apply {
        add(BnpCounter("messages", 1, 2, 3))
        add(BnpCounter("tasks", 2, 3, 4))
        add(BnpCounter("notifications", 3, 4, 5))
    }

    private val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        whenever(controller.getCountersCached()).thenReturn(cachedCounters)
        whenever(controller.dataRefreshed()).thenReturn(subscriptionEvent)
        whenever(subscriptionEvent.subscribe(any())).thenReturn(subscription)
        repository = CountersRepository(lazy { controller }, dispatcher)
    }

    @Test
    fun `When consumer subscribes, then it get cached values`() = dispatcher.runBlockingTest {
        val cached = repository.counters.first()
        val cachedValues = ArrayList(cached.values)
        assertEquals(cachedCounters, cachedValues)
    }
}