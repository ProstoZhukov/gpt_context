package ru.tensor.sbis.message_panel.viewModel.livedata.keyboard

import org.mockito.kotlin.*
import io.reactivex.Observable
import io.reactivex.subjects.Subject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

private const val KEYBOARD_OPENED_HEIGHT = 100

/**
 * @author vv.chekurda
 * @since 11/6/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class KeyboardEventHandlerImplTest {

    private val transition: Subject<Int> = mock { on { map<Int>(any()) } doReturn Observable.empty() }
    private val keyboard: Subject<Boolean> = mock()

    private val handler: KeyboardEventHandler = KeyboardEventHandlerImpl(KEYBOARD_OPENED_HEIGHT, transition, keyboard)

    @Before
    fun setUp() {
        // регистрация проверок для вызовов в конструкторе
        verify(transition).map<Int>(any())
    }

    @Test
    fun `Do nothing when user focus on edit text`() {
        handler.accept(OpenedByFocus)

        verifyNoMoreInteractions(transition, keyboard)
    }

    @Test
    fun `Do nothing when edit text lost focus`() {
        handler.accept(ClosedByFocus)

        verifyNoMoreInteractions(transition, keyboard)
    }

    @Test
    fun `When keyboard open requested first time, then focus should be requested and then transition should be applied`() {
        // только в этом тесте важно начинать с "чистого состояния"
        val handler = KeyboardEventHandlerImpl(DEFAULT_KEYBOARD_TRANSITION)
        val keyboard = handler.showKeyboard.test()
        val transition = handler.transitionY.test()

        handler.accept(OpenedByRequest)

        transition.assertNoValues()
        keyboard.assertValue(true)

        // клавиатура открылась после запроса фокуса
        handler.accept(OpenedByAdjustHelper(KEYBOARD_OPENED_HEIGHT))
        transition.assertValue(KEYBOARD_OPENED_HEIGHT.unaryMinus())
    }

    @Test
    fun `When keyboard open requested, then transition should be applied and then focus should be requested`() {
        val handler = KeyboardEventHandlerImpl(KEYBOARD_OPENED_HEIGHT)
        val observer = Observable.merge(handler.transitionY, handler.showKeyboard).test()

        handler.accept(OpenedByRequest)

        observer.assertValues(KEYBOARD_OPENED_HEIGHT.unaryMinus(), true)
    }

    @Test
    fun `When keyboard close requested, then transition should be applied and then focus should be cleared`() {
        val handler = KeyboardEventHandlerImpl()
        // когда-то был подъём
        handler.accept(OpenedByAdjustHelper(KEYBOARD_OPENED_HEIGHT))
        val observer = Observable.merge(handler.transitionY, handler.showKeyboard).test()

        handler.accept(ClosedByRequest)

        observer.assertValues(DEFAULT_KEYBOARD_TRANSITION, false)
    }

    @Test
    fun `Do nothing when keyboard opened after explicit request`() {
        handler.accept(OpenedByRequest)
        handler.accept(OpenedByAdjustHelper(KEYBOARD_OPENED_HEIGHT))

        verify(transition).onNext(KEYBOARD_OPENED_HEIGHT)
        verify(keyboard).onNext(true)
        verifyNoMoreInteractions(transition, keyboard)
    }

    @Test
    fun `Do nothing when keyboard closed after explicit request`() {
        val handler = KeyboardEventHandlerImpl(KEYBOARD_OPENED_HEIGHT)
        handler.accept(OpenedByRequest)

        val transitionObserver = handler.transitionY.test()
        val keyboardObserver = handler.showKeyboard.test()

        handler.accept(ClosedByRequest)
        handler.accept(ClosedByAdjustHelper(DEFAULT_KEYBOARD_TRANSITION))

        keyboardObserver.assertValueCount(1)
        transitionObserver.assertValueCount(1)
    }

    @Test
    fun `When keyboard closed by external rules (for example scroll), then transition and focus should be cleared`() {
        // когда-то был подъём клавиатуры
        handler.accept(OpenedByAdjustHelper(KEYBOARD_OPENED_HEIGHT))

        handler.accept(ClosedByAdjustHelper(DEFAULT_KEYBOARD_TRANSITION))

        verify(transition).onNext(DEFAULT_KEYBOARD_TRANSITION)
        verifyNoMoreInteractions(keyboard)
    }
}