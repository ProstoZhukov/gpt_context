package ru.tensor.sbis.message_panel.recorder.viewmodel

import ru.tensor.sbis.recorder.decl.RecordPermissionMediator

class RecordPermissionMediatorMock : RecordPermissionMediator {

    var allow = true

    override fun withPermission(block: () -> Unit) {
        if (allow) block()
    }
}