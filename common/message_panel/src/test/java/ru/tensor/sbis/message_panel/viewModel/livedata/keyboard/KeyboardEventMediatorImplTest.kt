package ru.tensor.sbis.message_panel.viewModel.livedata.keyboard

import io.mockk.mockk
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author vv.chekurda
 * @since 11/5/2019
 */
@RunWith(JUnitParamsRunner::class)
class KeyboardEventMediatorImplTest {

    private val openedByAdjustHelperEvent = OpenedByAdjustHelper(123)
    private val closedByAdjustHelperEvent = ClosedByAdjustHelper(321)

    private val mediator = KeyboardEventMediatorImpl()

    @Test
    fun `When keyboard event posted to mediator, then it should be delivered to observers`() {
        val event: KeyboardEvent = mockk()
        val eventObserver = mediator.keyboardState.test()

        mediator.postKeyboardEvent(event)

        eventObserver.assertValue(event)
    }

    @Test
    @Parameters(method = "getFocusStateParameters")
    fun `Verify focus state`(event: KeyboardEvent, hasFocus: Boolean) {
        val focusObserver = mediator.hasFocus.test()

        mediator.postKeyboardEvent(event.guard())

        focusObserver.assertValue(hasFocus)
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=563acef6-e856-4f46-bcaf-36edfb6f18b8
     */
    @Test
    fun `When OpenedByFocus event received, then focus should be cleared only by ClosedByFocus`() {
        val focusObserver = mediator.hasFocus.test()
        // предварительно установим фокус
        mediator.postKeyboardEvent(OpenedByFocus)

        mediator.postKeyboardEvent(ClosedByFocus)

        focusObserver.assertValues(true, false)
    }

    @Test
    @Parameters(method = "getFocusIgnoredEvents")
    fun `When keyboard even is not OpenedByFocus or ClosedByFocus, then focus should not be changed`(
        event: KeyboardEvent
    ) {
        val focusObserver = mediator.hasFocus.test()

        mediator.postKeyboardEvent(event.guard())

        focusObserver.assertNoValues()
    }

    private fun getFocusStateParameters() = arrayOf(arrayOf(OpenedByFocus, true), arrayOf(ClosedByFocus, false))

    private fun getFocusIgnoredEvents() = arrayOf(
        OpenedByRequest, ClosedByRequest, openedByAdjustHelperEvent, closedByAdjustHelperEvent
    )

    /**
     * Метод зациты тестов от расширения класса [KeyboardEvent]
     */
    private fun KeyboardEvent.guard(): KeyboardEvent = when(this) {
        OpenedByFocus           -> this
        ClosedByFocus           -> this
        OpenedByRequest         -> this
        ClosedByRequest         -> this
        is OpenedByAdjustHelper -> this
        is ClosedByAdjustHelper -> this
    }
}