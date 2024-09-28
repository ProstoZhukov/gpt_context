package ru.tensor.sbis.communicator_support_channel_list.presentation

import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.common.util.AdjustResizeHelper

internal interface KeyboardEventListenerChildPropagate :
    AdjustResizeHelper.KeyboardEventListener {

    fun getChildFragmentManager(): FragmentManager

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        getChildFragmentManager().fragments.forEach {
            (it as? AdjustResizeHelper.KeyboardEventListener)?.onKeyboardOpenMeasure(keyboardHeight)
        }
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        getChildFragmentManager().fragments.forEach {
            (it as? AdjustResizeHelper.KeyboardEventListener)?.onKeyboardCloseMeasure(keyboardHeight)
        }
        return true

    }
}