package ru.tensor.sbis.design.message_panel.vm.keyboard

import ru.tensor.sbis.common.util.AdjustResizeHelper

/**
 * @author vv.chekurda
 */
interface MessagePanelKeyboardApi : AdjustResizeHelper.KeyboardEventListener {

    fun showKeyboard()

    fun hideKeyboard()
}