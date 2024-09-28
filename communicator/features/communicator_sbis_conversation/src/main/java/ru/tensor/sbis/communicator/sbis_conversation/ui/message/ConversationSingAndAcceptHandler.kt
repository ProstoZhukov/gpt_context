package ru.tensor.sbis.communicator.sbis_conversation.ui.message

import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.generated.DocumentAccessType

interface ConversationSingAndAcceptHandler {

    /** @SelfDocumented */
    fun messageFileSigningSuccess()

    /** @SelfDocumented */
    fun messageFileSigningFailure()

    /**
     * Предоставить доступ к файлу согласно уровню доступа [accessType]
     */
    fun acceptAccessRequest(message: Message, messagePosition: Int, accessType: DocumentAccessType)
}