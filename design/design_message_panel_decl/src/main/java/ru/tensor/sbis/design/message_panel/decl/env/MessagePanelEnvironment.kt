package ru.tensor.sbis.design.message_panel.decl.env

import java.util.*

/**
 * Параметры окружения, в котором работает панель ввода
 *
 * @author ma.kolpakov
 */
interface MessagePanelEnvironment {
    val conversationUuid: UUID
    val documentUuid: UUID?
    val folderUuid: UUID?
}
