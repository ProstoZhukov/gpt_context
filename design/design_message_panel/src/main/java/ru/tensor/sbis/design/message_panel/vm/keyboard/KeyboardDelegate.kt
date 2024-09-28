package ru.tensor.sbis.design.message_panel.vm.keyboard

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.design.message_panel.view.layout.MessagePanelEditText

/**
 * Делегат для работы с клавиатурой.
 *
 * @author vv.chekurda
 */
internal interface KeyboardDelegate : MessagePanelKeyboardApi {

    val keyboardHeight: StateFlow<Int>

    fun onBottomOffsetChanged(offset: Int)

    /**
     * Инициализировать делегат.
     */
    fun init(scope: CoroutineScope, isEnabled: StateFlow<Boolean>)

    /**
     * Присоединить view поля ввода.
     */
    fun attachInputView(inputView: MessagePanelEditText)

    /**
     * Отсоединить view поля ввода.
     */
    fun detachInputView()
}