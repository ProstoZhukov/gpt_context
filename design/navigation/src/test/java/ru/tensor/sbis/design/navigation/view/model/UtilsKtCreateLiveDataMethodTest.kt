package ru.tensor.sbis.design.navigation.view.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.only
import org.mockito.kotlin.verify

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class UtilsKtCreateLiveDataMethodTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var dataA: Any

    @Mock
    private lateinit var dataB: Any

    @Mock
    private lateinit var observerA: Observer<Any>

    @Mock
    private lateinit var observerB: Observer<Any>

    private val subject = PublishSubject.create<Any>()

    private lateinit var liveData: LiveData<Any>

    @Before
    fun setUp() {
        liveData = createLiveData(subject)
    }

    @Test
    fun `When observable emmit value, then it should be delivered to live data observer`() {
        liveData.observeForever(observerA)

        subject.onNext(dataA)

        verify(observerA).onChanged(dataA)
    }

    @Test
    fun `When live data doesn't have any observers, then new observer should get latest value`() {
        subject.onNext(dataA)
        subject.onNext(dataB)

        liveData.observeForever(observerA)

        verify(observerA, only()).onChanged(dataB)
    }

    @Test
    fun `When live data's observer changed, then new observer should receive latest value`() {
        liveData.observeForever(observerA)

        subject.onNext(dataA)
        subject.onNext(dataB)

        liveData.removeObserver(observerA)
        liveData.observeForever(observerB)

        verify(observerB, only()).onChanged(dataB)
    }

    @Test
    fun `When observable emmit error, then no exception should be thrown`() {
        liveData.observeForever(observerA)

        subject.onNext(dataA)
        subject.onError(IllegalStateException("Test error message"))

        verify(observerA, only()).onChanged(dataA)
    }
}