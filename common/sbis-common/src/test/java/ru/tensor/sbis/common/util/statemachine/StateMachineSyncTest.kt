@file:Suppress("NonAsciiCharacters")

package ru.tensor.sbis.common.util.statemachine


import org.mockito.kotlin.mock
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import org.mockito.kotlin.verify

/**
 * @author Dmitry Subbotenko
 */
internal class StateMachineSyncTest {

    internal class DisabledStateEvent : SessionStateEvent
    internal class EventDisable : SessionEvent
    class DisabledState : SessionState() {
        init {
            event(EventEnable::class) { fire(EnabledStateEvent()) }
        }
    }

    internal class EnabledStateEvent : SessionStateEvent
    internal class EventEnable : SessionEvent
    open class EnabledState : SessionState() {
        init {
            event(EventDisable::class) { fire(DisabledStateEvent()) }
        }
    }

    class StateMachineTest : StateMachineInner by StateMachineImpl(Schedulers.trampoline(), Schedulers.trampoline()) {
        init {
//            enableLogging("test") { println(it) }
            state(DisabledStateEvent::class) { setState(DisabledState()) }
            state(EnabledStateEvent::class) { setState(EnabledState()) }
            setState(DisabledState())
        }
    }

    internal class TestEvent : SessionEvent


    @Test
    fun `стейт машина верно переключает состояния`() {
        val stateMachine = StateMachineTest()

        val testSubscriber = stateMachine.currentStateObservable
            .test()


        stateMachine.fire(EventEnable())
        stateMachine.fire(EventDisable())
        stateMachine.stop()

        testSubscriber.assertNoErrors()
            .awaitCount(3)
            .assertValueAt(0) { it is DisabledState }
            .assertValueAt(1) { it is EnabledState }
            .assertValueAt(2) { it is DisabledState }
            .assertComplete()

    }


    @Test
    fun `верный порядок иницианизации стейта`() {

        val setFunction = mock<() -> Unit>()
        val eventFunction = mock<(t: Any) -> Unit>()

        val newState = object : SessionState() {
            init {
                addOnSetAction(setFunction)
                event(TestEvent::class, eventFunction)
            }
        }

        val stateMachine =
            object : StateMachineInner by StateMachineImpl(Schedulers.trampoline(), Schedulers.trampoline()) {
                init {
                    state(EnabledStateEvent::class) { setState(newState) }
                    setState(DisabledState())
                }
            }

        stateMachine.fire(EventEnable())

        val event = TestEvent()
        stateMachine.fire(event)

        verify(setFunction).invoke()
        verify(eventFunction).invoke(event)
    }
}