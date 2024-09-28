package ru.tensor.sbis.message_panel.viewModel.livedata.keyboard

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

/**
 * @author vv.chekurda
 * @since 11/5/2019
 */
internal class KeyboardEventMediatorImpl : KeyboardEventMediator {

    override val keyboardState = BehaviorSubject.create<KeyboardEvent>()
    override val hasFocus: Observable<Boolean> = keyboardState
        .filter { it is OpenedByFocus || it is ClosedByFocus }
        .map { it is OpenedByFocus }

    override fun postKeyboardEvent(event: KeyboardEvent) = keyboardState.onNext(event)
}