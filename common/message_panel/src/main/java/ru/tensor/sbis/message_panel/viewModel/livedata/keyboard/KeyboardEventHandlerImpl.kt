package ru.tensor.sbis.message_panel.viewModel.livedata.keyboard

import androidx.annotation.Px
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

internal const val DEFAULT_KEYBOARD_TRANSITION = 0

/**
 * @author vv.chekurda
 * @since 11/1/2019
 */
internal class KeyboardEventHandlerImpl(
    @Px private var keyboardHeight: Int = DEFAULT_KEYBOARD_TRANSITION,
    private val transitionSubject: Subject<Int> = PublishSubject.create(),
    private val keyboardSubject: Subject<Boolean> = PublishSubject.create(),
    private val autoTranslateOnFocus: Boolean = false
) : KeyboardEventHandler {

    private var keyboardOpened = false
    private var lastHeight = DEFAULT_KEYBOARD_TRANSITION

    override val transitionY: Observable<Int> = transitionSubject.map(Int::unaryMinus)
    override val showKeyboard: Observable<Boolean> = keyboardSubject

    override fun accept(event: KeyboardEvent) = when (event) {
        OpenedByFocus -> {
            if (autoTranslateOnFocus) transitionSubject.onNext(keyboardHeight)
            keyboardOpened = true
            /* фокус средствами android - всё само */
        }
        ClosedByFocus -> {
            /* фокус средствами android - всё само */
        }
        OpenedByRequest -> {
            // если высота клавиатуры ещё не определена, дождмся состояния OpenedByAdjustHelper
            updateHeight(keyboardHeight)
            updateKeyboardState(true)
        }
        is OpenedByAdjustHelper -> {
            // высоту клавиатуры нужно обновить в любом случае
            keyboardHeight = event.height
            keyboardOpened = true
            updateHeight(keyboardHeight)
        }
        ClosedByRequest -> {
            updateHeight(DEFAULT_KEYBOARD_TRANSITION)
            updateKeyboardState(false)
        }
        is ClosedByAdjustHelper -> {
            keyboardOpened = false
            updateHeight(DEFAULT_KEYBOARD_TRANSITION)
        }
    }

    private fun updateHeight(newHeight: Int) {
        if (lastHeight != newHeight) {
            lastHeight = newHeight
            transitionSubject.onNext(newHeight)
        }
    }

    private fun updateKeyboardState(newState: Boolean) {
        if (keyboardOpened != newState) {
            keyboardOpened = newState
            keyboardSubject.onNext(newState)
        }
    }
}