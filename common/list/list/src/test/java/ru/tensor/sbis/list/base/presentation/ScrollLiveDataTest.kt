package ru.tensor.sbis.list.base.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle.State.CREATED
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import org.mockito.kotlin.mock
import org.mockito.kotlin.only
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.random.Random

/**
 * @author ma.kolpakov
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class ScrollLiveDataTest {

    private val scroll = Random.nextInt(Int.MAX_VALUE)

    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var observer: Observer<Int>

    @Mock
    private lateinit var lifecycleOwner: LifecycleOwner

    private lateinit var lifecycle: LifecycleRegistry

    private val liveData = ScrollLiveData()

    @Before
    fun setUp() {
        lifecycle = LifecycleRegistry(lifecycleOwner)
        whenever(lifecycleOwner.lifecycle).thenReturn(lifecycle)

        liveData.observe(lifecycleOwner, observer)
    }

    @Test
    fun `When value updated, then observer gets it`() {
        liveData.value = scroll
        lifecycle.currentState = STARTED

        verify(observer, only()).onChanged(scroll)
    }

    @Test
    fun `Given paused state, when state become started again, then observer should not get value`() {
        liveData.value = scroll
        lifecycle.currentState = STARTED
        lifecycle.currentState = CREATED
        lifecycle.currentState = STARTED

        verify(observer, only()).onChanged(scroll)
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=cd6b5e05-f9c1-4054-80b8-81961a55fbc8
     */
    @Test
    fun `Given paused state, when state become started with new observer, then new observer should not get value`() {
        val anotherObserver: Observer<Int> = mock()

        liveData.value = scroll
        lifecycle.currentState = STARTED
        lifecycle.currentState = CREATED

        // восстановление состояния после поворота
        liveData.observe(lifecycleOwner, anotherObserver)
        lifecycle.currentState = STARTED

        verify(observer, only()).onChanged(scroll)
        verifyNoMoreInteractions(anotherObserver)
    }
}