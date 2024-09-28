package ru.tensor.sbis.design.message_panel.vm.state

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import ru.tensor.sbis.design.message_panel.R

/**
 * @author ma.kolpakov
 */
internal class StateDelegateImpl @Inject constructor() : StateDelegate {

    override val isEnabled = MutableStateFlow(false)

    override val hint = MutableStateFlow(R.string.design_message_panel_enter_message_hint)

    override val isNewDialog = MutableStateFlow(false)

    override fun setEnabled(enabled: Boolean, hintRes: Int) {
        isEnabled.value = enabled
        setHint(hintRes)
    }

    override fun setNewDialog(newDialog: Boolean) {
        isNewDialog.value = newDialog
    }

    override fun setHint(hintRes: Int) {
        hint.value = hintRes
    }
}