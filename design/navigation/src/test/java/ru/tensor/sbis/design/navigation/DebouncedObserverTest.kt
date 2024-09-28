package ru.tensor.sbis.design.navigation

import io.reactivex.functions.Consumer
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.whenever
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.design.navigation.view.DebouncedObserver

@RunWith(MockitoJUnitRunner.StrictStubs::class)
class DebouncedObserverTest {

    @get:Rule
    val rxSchedulerRule = TrampolineSchedulerRule()

    @field:Mock
    private lateinit var event: Any

    @field:Mock
    private lateinit var resultConsumer: Consumer<Any?>

    @field:Mock
    private lateinit var errorConsumer: Consumer<Throwable>

    private lateinit var debouncedObserver: DebouncedObserver<Any>

    @Before
    fun setUp() {
        debouncedObserver = DebouncedObserver(resultConsumer, 100L, errorConsumer)
    }

    @Test
    fun `deliver single event`() {
        debouncedObserver.onChanged(event)

        verify(resultConsumer, only()).accept(event)
        verify(errorConsumer, never()).accept(any(Throwable::class.java))
    }

    @Test
    fun `deliver same event without delay first event`() {
        debouncedObserver.onChanged(event)
        debouncedObserver.onChanged(event)

        verify(resultConsumer, times(2)).accept(event)
        verify(errorConsumer, never()).accept(any(Throwable::class.java))
    }

    @Test
    fun `null values supported`() {
        debouncedObserver.onChanged(null)

        verify(resultConsumer, only()).accept(null)
        verify(errorConsumer, never()).accept(any(Throwable::class.java))
    }

    @Test
    fun `When consumer failed, then error consumer get the error`() {
        val expected = Exception("Expected fail")
        val exceptionCaptor = argumentCaptor<Exception>()
        whenever(resultConsumer.accept(event)).thenThrow(expected)

        debouncedObserver.accept(event)

        verify(errorConsumer).accept(exceptionCaptor.capture())
        assertSame(expected, exceptionCaptor.allValues.single().cause)
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=ad1ae3c2-71b5-40fc-81f6-e0cddb9038c7
     */
    @Test
    fun `When consumer failed, then events flow should be alive`() {
        val expected = Exception("Expected fail")
        whenever(resultConsumer.accept(event)).thenThrow(expected)

        debouncedObserver.accept(event)
        debouncedObserver.accept(event)

        verify(resultConsumer, times(2)).accept(event)
    }
}