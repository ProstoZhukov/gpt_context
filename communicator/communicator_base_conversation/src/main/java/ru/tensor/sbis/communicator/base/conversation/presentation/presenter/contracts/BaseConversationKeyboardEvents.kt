package ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts

/**
 * Контракт базовых событий клавиатуры.
 *
 * @author vv.chekurda
 */
interface BaseConversationKeyboardEvents {

    /**
     * Клавиатура поднимается.
     * @param keyboardHeight высота клавиатуры.
     */
    fun onKeyboardAppears(keyboardHeight: Int)

    /**
     * Клавиатура опускается.
     * @param keyboardHeight прежняя высота клавиатуры.
     */
    fun onKeyboardDisappears(keyboardHeight: Int)
}