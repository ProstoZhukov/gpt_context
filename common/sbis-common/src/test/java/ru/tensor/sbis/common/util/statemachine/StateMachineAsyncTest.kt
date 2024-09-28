@file:Suppress("NonAsciiCharacters", "NonAsciiCharacters")

package ru.tensor.sbis.common.util.statemachine

import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import io.reactivex.observers.BaseTestConsumer
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


/**
 * @author Dmitry Subbotenko
 */
internal class StateMachineAsyncTest {

    internal class DisabledStateEvent : SessionStateEvent
    internal class EventDisable : SessionEvent
    class DisabledState : SessionState() {
        init {
            event(EventEnable::class) { fire(EnabledStateEvent()) }
        }
    }

    internal class EnabledStateEvent : SessionStateEvent
    internal class EventEnable : SessionEvent
    class EnabledState : SessionState() {
        init {
            event(EventDisable::class) { fire(DisabledStateEvent()) }
        }
    }

    class StateMachineTest : StateMachineInner by StateMachineImpl(Schedulers.single(), Schedulers.computation()) {
        init {
//            enableLogging("test") { println(it) }
            state(DisabledStateEvent::class) { setState(DisabledState()) }
            state(EnabledStateEvent::class) { setState(EnabledState()) }
            setState(DisabledState())
        }
    }


    /**
     * https://online.sbis.ru/opendoc.html?guid=b45cff7e-ad78-4fc3-a121-b2eba0ec9bcc
     */
    //@Test
    // TODO https://online.sbis.ru/opendoc.html?guid=bf7ca78f-a410-41f4-8b48-82b66442606a
    // TODO https://online.sbis.ru/opendoc.html?guid=4010c60f-aa4a-4723-a26f-7ff20f816134
    fun `стейт машина верно переключает состояния при синхронной работе`() {
        val stateMachine = StateMachineTest()

        val testSubscriber = stateMachine.currentStateObservable
            .subscribeOn(Schedulers.single())
            .test()


        stateMachine.fire(EventEnable())
        stateMachine.fire(EventDisable())


        testSubscriber.assertNoErrors()
            .awaitCount (3, BaseTestConsumer.TestWaitStrategy.SLEEP_100MS, 50000)
            .assertValueAt(0) { it is DisabledState }
            .assertValueAt(1) { it is EnabledState }
            .assertValueAt(2) { it is DisabledState }

    }


    class Event1 : SessionEvent
    class Event2 : SessionEvent
    class Event3 : SessionEvent
    class Event4 : SessionEvent
    class Event5 : SessionEvent


    class State1 : SessionState() {
        companion object {
            val eventFunction = mock<(SessionEvent) -> Unit>()
        }

        init {
            addOnSetAction { Thread.sleep(30) }
            event(Event1::class) { eventFunction(it); Thread.sleep(10) }
            event(Event2::class) { eventFunction(it); Thread.sleep(10) }
            event(Event3::class) { eventFunction(it); Thread.sleep(10); fire(SetState2()) }
            event(Event4::class) { eventFunction(it); Thread.sleep(10) }
            event(Event5::class) { eventFunction(it); Thread.sleep(10) }
        }
    }

    class SetState2 : SessionStateEvent
    class State2 : SessionState() {
        companion object {
            val eventFunction = mock<(SessionEvent) -> Unit>()
            val latch = CountDownLatch(1)
        }

        init {
            addOnSetAction { Thread.sleep(30) }
            event(Event1::class) { eventFunction(it); Thread.sleep(10) }
            event(Event2::class) { eventFunction(it); Thread.sleep(10) }
            event(Event3::class) { eventFunction(it); Thread.sleep(10) }
            event(Event4::class) { eventFunction(it); Thread.sleep(10) }
            event(Event5::class) { eventFunction(it); Thread.sleep(10); latch.countDown() }
        }
    }

    /**
     * https://online.sbis.ru/opendoc.html?guid=34a2e63b-c4c0-435c-b898-0907d77eaec5
     */
    @Test
    fun `при асинхронной работе события приходят именно в тот стейт в контексте которого были вызваны`() {
        val stateMachine =
            object : StateMachineInner by StateMachineImpl(Schedulers.single(), Schedulers.computation()) {
                init {
//                        enableLogging("test") { println(it) }
                    state(SetState2::class) { setState(State2()) }
                    setState(State1())
                }
            }

        val event1 = Event1()
        val event2 = Event2()
        val event3 = Event3()
        val event4 = Event4()
        val event5 = Event5()
        stateMachine.fire(event1)
        stateMachine.fire(event2)
        stateMachine.fire(event3)
        stateMachine.fire(event4)
        stateMachine.fire(event5)

        State2.latch.await(1, TimeUnit.SECONDS)

        verify(State1.eventFunction).invoke(event1)
        verify(State1.eventFunction).invoke(event2)
        verify(State1.eventFunction).invoke(event3)
        verify(State1.eventFunction, never()).invoke(event4)
        verify(State1.eventFunction, never()).invoke(event5)

        verify(State2.eventFunction, never()).invoke(event1)
        verify(State2.eventFunction, never()).invoke(event2)
        verify(State2.eventFunction, never()).invoke(event3)
        verify(State2.eventFunction).invoke(event4)
        verify(State2.eventFunction).invoke(event5)
    }
}