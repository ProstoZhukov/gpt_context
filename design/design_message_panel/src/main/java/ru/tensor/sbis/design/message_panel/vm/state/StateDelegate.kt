package ru.tensor.sbis.design.message_panel.vm.state

import androidx.annotation.StringRes

/**
 * @author ma.kolpakov
 */
internal interface StateDelegate : MessagePanelStateApi {

    fun setNewDialog(newDialog: Boolean)

    fun setHint(@StringRes hintRes: Int)
}