package ru.tensor.sbis.design.message_panel.domain

import ru.tensor.sbis.design.message_panel.decl.env.MessagePanelEnvironment
import ru.tensor.sbis.design.message_panel.decl.env.MessagePanelEnvironmentModel
import ru.tensor.sbis.design.message_panel.vm.MessagePanelViewModel
import ru.tensor.sbis.design.message_panel.decl.MessagePanelUseCase

/**
 * @author ma.kolpakov
 */
sealed class AbstractMessagePanelUseCase(
    private val environment: MessagePanelEnvironmentModel
) : MessagePanelUseCase, MessagePanelEnvironment by environment {

    internal abstract suspend fun setup(vm: MessagePanelViewModel)

    internal abstract suspend fun send()

    internal open suspend fun save() = Unit

    override fun toString() = "${javaClass.simpleName}($environment)"
}
