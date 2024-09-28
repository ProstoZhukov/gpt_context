package ru.tensor.sbis.counter_provider

import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.tensor.sbis.counter_provider.AbstractCounterProviderSyncTest.Companion.MIN_TIMEOUT
import ru.tensor.sbis.counter_provider.AbstractCounterProviderSyncTest.Companion.subscribeAndLogError
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class AbstractCounterProviderAsyncTest {

    private lateinit var testRepository: AbstractCounterProviderSyncTest.TestRepository
    private lateinit var testCounterProvider: AbstractCounterProvider<Int, AbstractCounterProviderSyncTest.TestRepository>

    @Before
    fun setup() {
        testRepository = AbstractCounterProviderSyncTest.TestRepository()
        testCounterProvider = AbstractCounterProvider(testRepository)
    }

    @Test(timeout = MIN_TIMEOUT * 10)
    fun integrationTest() {
        val semaphore = Semaphore(3, true).apply {
            acquire(3)
        }
        // имитация главного потока
        val scheduler = Schedulers.newThread()


        var result1 = -1
        var result2 = -1
        // математика любит тройной контроль
        var result3 = -1

        val disposable1 = testCounterProvider.counterEventObservable.observeOn(scheduler).subscribeAndLogError {
            result1 = it
            semaphore.release()
        }
        val disposable2 = testCounterProvider.counterEventObservable.observeOn(scheduler).subscribeAndLogError {
            result2 = it
            semaphore.release()
        }
        val disposable3 = testCounterProvider.counterEventObservable.observeOn(scheduler).subscribeAndLogError {
            result3 = it
            semaphore.release()
        }

        assertTrue(semaphore.tryAcquire(3, MIN_TIMEOUT, TimeUnit.MILLISECONDS))
        assertEquals(1, testRepository.callbacks.size)
        assertTrue(testRepository.callbacks.keys.first().enabled)
        assertEquals(0, result1)
        assertEquals(0, result2)
        assertEquals(0, result3)

        testRepository.incrementAndNotify()

        assertTrue(semaphore.tryAcquire(3, MIN_TIMEOUT, TimeUnit.MILLISECONDS))
        assertEquals(1, result1)
        assertEquals(1, result2)
        assertEquals(1, result3)


        disposable1.dispose()
        disposable2.dispose()

        testRepository.incrementAndNotify()
        assertTrue(semaphore.tryAcquire(1, MIN_TIMEOUT, TimeUnit.MILLISECONDS))

        assertEquals(1, testRepository.callbacks.size)
        assertTrue(testRepository.callbacks.keys.first().enabled)
        assertEquals(2, result3)

        disposable3.dispose()

        assertTrue(spinningCheck(MIN_TIMEOUT) {
            !testRepository.callbacks.keys.first().enabled
        })

        assertEquals(1, testRepository.callbacks.size)


        var result4 = -1
        testCounterProvider.counterEventObservable.observeOn(scheduler).subscribeAndLogError {
            result4 = it
            semaphore.release()
        }

        assertTrue(semaphore.tryAcquire(1, MIN_TIMEOUT, TimeUnit.MILLISECONDS))
        assertEquals(1, testRepository.callbacks.size)
        assertTrue(testRepository.callbacks.keys.first().enabled)
        assertEquals(2, result4)

        testRepository.incrementAndNotify()

        assertTrue(semaphore.tryAcquire(1, MIN_TIMEOUT, TimeUnit.MILLISECONDS))
        assertEquals(3, result4)
    }

    private fun spinningCheck(timeOut: Long, check: () -> Boolean): Boolean {
        var left = timeOut

        while (!check() && left > 0) {
            Thread.sleep(10)
            left -= 10
        }

        return check()
    }
}