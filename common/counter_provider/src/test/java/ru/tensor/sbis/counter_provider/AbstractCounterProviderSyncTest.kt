package ru.tensor.sbis.counter_provider

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import ru.tensor.sbis.crud.generated.DataRefreshCallback
import ru.tensor.sbis.platform.generated.Subscription
import java.util.concurrent.atomic.AtomicInteger

class AbstractCounterProviderSyncTest {
    private lateinit var testRepository: TestRepository
    private lateinit var testCounterProvider: AbstractCounterProvider<Int, TestRepository>

    @Before
    fun setup() {
        testRepository = TestRepository()
        testCounterProvider = AbstractCounterProvider(testRepository, Schedulers.trampoline())
    }


    @Test
    fun getCounter() {
        assertEquals(0, testCounterProvider.counter)
        testRepository.incrementAndNotify()
        assertEquals(1, testCounterProvider.counter)

        testRepository.subscribe(createCallback {
            // NOP
        })

        assertEquals(1, testCounterProvider.counter)
        testRepository.incrementAndNotify()
        assertEquals(2, testCounterProvider.counter)
    }


    @Test(timeout = MIN_TIMEOUT * 3)
    fun deliverCounterOnSubscribe() {
        testRepository.testCounter.incrementAndGet()
        val result = testCounterProvider.counterEventObservable.blockingFirst()
        assertEquals(1, result)
    }

    @Test(timeout = MIN_TIMEOUT * 3)
    fun deliverCounterToSingleSubscriber() {
        var result = -1
        testCounterProvider.counterEventObservable.subscribeAndLogError {
            result = it
        }
        testRepository.incrementAndNotify()
        assertEquals(1, result)
    }

    @Test
    fun integrationTest() {
        var result1 = -1
        var result2 = -1
        // математика любит тройной контроль
        var result3 = -1

        val disposable1 = testCounterProvider.counterEventObservable.subscribeAndLogError {
            result1 = it
        }
        assertEquals(1, testRepository.callbacks.size)
        assertTrue(testRepository.callbacks.keys.first().enabled)

        val disposable2 = testCounterProvider.counterEventObservable.subscribeAndLogError {
            result2 = it
        }
        val disposable3 = testCounterProvider.counterEventObservable.subscribeAndLogError {
            result3 = it
        }

        // Независимо от количества конечных подписчиков, подписка на контроллер должна быть одна
        assertEquals(1, testRepository.callbacks.size)
        assertTrue(testRepository.callbacks.keys.first().enabled)

        testRepository.incrementAndNotify()
        assertEquals(1, testRepository.callbacks.size)
        assertTrue(testRepository.callbacks.keys.first().enabled)
        assertEquals(1, result1)
        assertEquals(1, result2)
        assertEquals(1, result3)

        disposable1.dispose()
        disposable2.dispose()
        assertEquals(1, testRepository.callbacks.size)
        assertTrue(testRepository.callbacks.keys.first().enabled)

        testRepository.incrementAndNotify()
        assertEquals(2, result3)

        disposable3.dispose()
        assertEquals(1, testRepository.callbacks.size)
        assertFalse(testRepository.callbacks.keys.first().enabled)

        var result4 = -1
        testCounterProvider.counterEventObservable.subscribeAndLogError {
            result4 = it
        }
        assertEquals(1, testRepository.callbacks.size)
        assertTrue(testRepository.callbacks.keys.first().enabled)
        assertEquals(2, result4)

        testRepository.incrementAndNotify()
        assertEquals(3, result4)
    }


    internal class TestRepository : CounterRepository<Int> {
        val callbacks: MutableMap<TestSubscription, DataRefreshCallback> = HashMap()
        val testCounter = AtomicInteger()

        @Synchronized
        fun incrementAndNotify() {
            testCounter.incrementAndGet()
            callbacks.forEach { (subscription, callback) ->
                if (subscription.enabled) callback.execute(hashMapOf())
            }
        }

        override fun getCounter() = testCounter.get()

        @Synchronized
        override fun subscribe(callback: DataRefreshCallback): Subscription {
            return TestSubscription().also {
                callbacks[it] = callback
            }
        }

        internal inner class TestSubscription : Subscription() {
            var enabled: Boolean = false
                @Synchronized private set
                @Synchronized get

            override fun enable() = synchronized(this@TestRepository) {
                enabled = true
            }

            override fun disable() = synchronized(this@TestRepository) {
                enabled = false
            }
        }
    }


    internal fun createCallback(block: () -> Unit): DataRefreshCallback = object : DataRefreshCallback() {
        override fun execute(p0: java.util.HashMap<String, String>) {
            block()
        }
    }

    companion object {
        // Из-за весьма тормознутой тест машины, увеличиваем таймауты с запасом
        const val MIN_TIMEOUT = 2000L

        internal fun <T> Observable<T>.subscribeAndLogError(onNext: (T) -> Unit): Disposable {
            return subscribe(onNext, Throwable::printStackTrace)
        }
    }
}
