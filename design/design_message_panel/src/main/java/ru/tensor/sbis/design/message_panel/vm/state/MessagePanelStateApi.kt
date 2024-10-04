package ru.tensor.sbis.design.message_panel.vm.state

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.design.message_panel.R

/**
 * @author ma.kolpakov
 */
interface MessagePanelStateApi {

    val isEnabled: StateFlow<Boolean>

    val hint: StateFlow<Int>

    val isNewDialog: StateFlow<Boolean>

    fun setEnabled(enabled: Boolean, @StringRes hintRes: Int = R.string.design_message_panel_enter_message_hint)
}