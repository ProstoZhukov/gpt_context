package ru.tensor.sbis.message_panel.viewModel.livedata.keyboard

import androidx.annotation.AnyThread
import io.reactivex.Observable
import ru.tensor.sbis.message_panel.view.MessagePanel

/**
 * Объект для синхронизации состояний клавиатуры [KeyboardEvent] и панели ввода [MessagePanel].
 * Посредник между внутренними событиями и внешними
 *
 * @author vv.chekurda
 * @since 11/1/2019
 */
interface KeyboardEventMediator {

    /**
     * Актуальное состояние клавиатуры
     */
    val keyboardState: Observable<KeyboardEvent>

    /**
     * Подписка на изменение состояния фокуса
     */
    val hasFocus: Observable<Boolean>

    /**
     * Публикация события об изменении состояния клавиатуры
     */
    @AnyThread
    fun postKeyboardEvent(event: KeyboardEvent)
}