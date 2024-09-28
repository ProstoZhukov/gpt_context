package ru.tensor.sbis.message_panel.recorder.viewmodel.listener

import ru.tensor.sbis.recorder.decl.RecordViewHintListener

internal object DEFAULT : RecordViewHintListener {
    override fun onShowHint(show: Boolean) = Unit
}
