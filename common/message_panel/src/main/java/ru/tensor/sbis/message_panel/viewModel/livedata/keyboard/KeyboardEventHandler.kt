package ru.tensor.sbis.message_panel.viewModel.livedata.keyboard

import io.reactivex.Observable
import io.reactivex.functions.Consumer

/**
 * Подписка на события [KeyboardEvent], которая инкапсулирует реакции компонентов панели ввода
 *
 * @author vv.chekurda
 * @since 11/6/2019
 */
internal interface KeyboardEventHandler : Consumer<KeyboardEvent> {

    val transitionY: Observable<Int>

    val showKeyboard: Observable<Boolean>
}