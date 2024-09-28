package ru.tensor.sbis.design.message_panel.decl.env

import java.util.*

/**
 * @author ma.kolpakov
 */
data class MessagePanelEnvironmentModel(
    override val conversationUuid: UUID,
    override val documentUuid: UUID? = null,
    override val folderUuid: UUID? = null
) : MessagePanelEnvironment
