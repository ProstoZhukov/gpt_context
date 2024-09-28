package ru.tensor.sbis.design.navigation.filter

import io.mockk.mockk
import org.junit.Test
import ru.tensor.sbis.design.navigation.view.filter.BackpressureEventFilterImpl
import ru.tensor.sbis.design.navigation.view.model.NavigationEvent

/**
 * @author ma.kolpakov
 * Создан 11/11/2019
 */
class BackpressureEventFilterImplTest {

    private val firstEvent: NavigationEvent<*> = mockk()
    private val secondEvent: NavigationEvent<*> = mockk()
    private val thirdEvent: NavigationEvent<*> = mockk()

    private val filter = BackpressureEventFilterImpl<NavigationEvent<*>>()
    private val observer = filter.eventObservable.test()

    @Test
    fun `First event should be delivered immediately`() {
        filter.onChanged(firstEvent)

        observer.assertValue(firstEvent)
    }

    @Test
    fun `When first event delivered, then events shouldn't be delivered until request`() {
        filter.onChanged(firstEvent)
        filter.onChanged(secondEvent)

        observer.assertValueCount(1)
    }

    @Test
    fun `When second event requested, then it should be delivered`() {
        filter.onChanged(firstEvent)
        filter.onChanged(secondEvent)

        observer.assertValue(firstEvent)

        filter.requestNext()

        observer.assertValues(firstEvent, secondEvent)
    }

    @Test
    fun `When observer waited for next event, then it should be delivered immediately`() {
        filter.onChanged(firstEvent)
        observer.assertValue(firstEvent)

        filter.requestNext()
        // событие доставлено после запроса -> сразу передать подписчикам
        filter.onChanged(secondEvent)

        observer.assertValues(firstEvent, secondEvent)
    }

    @Test
    fun `When event requested, then only one next event should be delivered`() {
        filter.onChanged(firstEvent)
        filter.onChanged(secondEvent)
        filter.requestNext()
        // третьего никто не ждёт
        filter.onChanged(thirdEvent)

        observer.assertValues(firstEvent, secondEvent)
    }

    @Test
    fun `When multiple events requested, then only one should be delivered`() {
        filter.onChanged(firstEvent)
        filter.requestNext()
        filter.requestNext()
        // доставляем только следующий, запросы перекрывают друг друга
        filter.onChanged(secondEvent)
        filter.onChanged(thirdEvent)

        observer.assertValues(firstEvent, secondEvent)
    }

    @Test
    fun `When filter has three events and request was after third event, then second event should be ignored`() {
        filter.onChanged(firstEvent)
        filter.onChanged(secondEvent)
        filter.onChanged(thirdEvent)

        filter.requestNext()

        observer.assertValues(firstEvent, thirdEvent)
    }

    @Test
    fun `When requested event is equal to previous one, then it shouldn't be delivered twice`() {
        filter.onChanged(firstEvent)
        filter.onChanged(secondEvent)
        filter.onChanged(firstEvent)

        filter.requestNext()

        observer.assertValue(firstEvent)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Nullable events unsupported`() {
        filter.onChanged(null)
    }
}