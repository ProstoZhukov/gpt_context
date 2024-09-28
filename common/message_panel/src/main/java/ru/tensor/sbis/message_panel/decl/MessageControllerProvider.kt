package ru.tensor.sbis.message_panel.decl

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик контроллера сообщений
 *
 * @author kv.martyshenko
 */
fun interface MessageControllerProvider : Feature {
    fun getMessageController(): DependencyProvider<MessageController>
}