package ru.tensor.sbis.crud3.data

import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.service.generated.IndexPair
import ru.tensor.sbis.service.generated.StubType.SERVER_TROUBLE
import ru.tensor.sbis.service.generated.ViewPosition.HEADER
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
internal class Crud3ObserverCallbackImplTest {

    private val crudObserverCallback = Crud3ObserverCallbackImpl<String, String>()
    private val testObserver = TestObserver<CollectionEvent<String, String>>()

    @Before
    fun setUp() {
        crudObserverCallback.events.subscribe(testObserver)
    }

    @Test
    fun onReset() {
        crudObserverCallback.onReset(listOf("item1", "item2"))

        //assert
        testObserver.assertSingleValue()
        testObserver.assertValue { it is OnReset }
    }

    @Test
    fun onRemove() {
        crudObserverCallback.onRemove(listOf(1L, 2L))

        //assert
        testObserver.assertSingleValue()
        testObserver.assertValue { it is OnRemove }
    }

    @Test
    fun onMove() {
        crudObserverCallback.onMove(listOf(IndexPair(1, 2)))

        //assert
        testObserver.assertSingleValue()
        testObserver.assertValue { it is OnMove }
    }

    @Test
    fun onAdd() {
        crudObserverCallback.onAdd(listOf("item1", "item2"))

        //assert
        testObserver.assertSingleValue()
        testObserver.assertValue { it is OnAdd }
    }

    @Test
    fun onReplace() {
        crudObserverCallback.onReplace(listOf("item1", "item2"))

        //assert
        testObserver.assertSingleValue()
        testObserver.assertValue { it is OnReplace }
    }

    @Test
    fun onAddThrobber() {
        crudObserverCallback.onAddThrobber(HEADER)

        //assert
        testObserver.assertSingleValue()
        testObserver.assertValue { it is OnAddThrobber }
        testObserver.assertValue { it is OnAddThrobber && it.position == HEADER }
    }

    @Test
    fun onRemoveThrobber() {
        crudObserverCallback.onRemoveThrobber()

        //assert
        testObserver.assertSingleValue()
        testObserver.assertValue { it is OnRemoveThrobber }
    }

    @Test
    fun onAddStub() {
        crudObserverCallback.onAddStub(SERVER_TROUBLE, HEADER)

        //assert
        testObserver.assertSingleValue()
        testObserver.assertValue { it is OnAddStub }
        testObserver.assertValue { it is OnAddStub && it.position == HEADER && it.stubType == SERVER_TROUBLE }
    }

    @Test
    fun onRemoveStub() {
        crudObserverCallback.onRemoveStub()

        //assert
        testObserver.assertSingleValue()
        testObserver.assertValue { it is OnRemoveStub }
    }

    private fun <T> TestObserver<T>.assertSingleValue(): TestObserver<T> {
        awaitTerminalEvent(1, TimeUnit.SECONDS)
        assertNoErrors()
        assertValueCount(1)
        return this
    }
}