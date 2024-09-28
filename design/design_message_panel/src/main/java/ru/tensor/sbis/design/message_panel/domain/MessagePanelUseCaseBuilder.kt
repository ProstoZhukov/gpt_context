package ru.tensor.sbis.design.message_panel.domain

import android.content.Context
import android.net.Uri
import ru.tensor.sbis.design.message_panel.decl.env.MessagePanelEnvironmentModel
import ru.tensor.sbis.design.message_panel.di.MessagePanelComponent
import ru.tensor.sbis.design.message_panel.di.usecase.MessagePanelUseCaseComponent
import ru.tensor.sbis.design.message_panel.vm.usecase.MessagePanelUseCaseApi
import java.util.*

/**
 * Строитель для сценариев работы панели ввода. Экземпляр поставляется со всеми необходимыми
 * зависимостями из графа уровня приложения [MessagePanelComponent] при обнолении сценария
 * работы в качестве ресивера в методе [MessagePanelUseCaseApi.updateUseCase]
 *
 * @author ma.kolpakov
 */
class MessagePanelUseCaseBuilder internal constructor(
    private val appContext: Context,
    private val messagePanelUseCaseComponent: MessagePanelUseCaseComponent.Factory
) {

    fun buildSendMessageUseCase(conversationUuid: UUID): AbstractMessagePanelUseCase =
        SendMessageUseCase(
            MessagePanelEnvironmentModel(conversationUuid = conversationUuid),
            messagePanelUseCaseComponent
        )

    fun buildCreateNewDialogWithMessageUseCase(conversationUuid: UUID): AbstractMessagePanelUseCase =
        CreateDialogWithMessageUseCase(
            MessagePanelEnvironmentModel(conversationUuid = conversationUuid),
            messagePanelUseCaseComponent
        )

    fun buildEditMessageUseCase(
        conversationUuid: UUID,
        editingMessageUuid: UUID,
        editedText: String
    ): AbstractMessagePanelUseCase =
        EditMessageUseCase(
            editingMessageUuid,
            editedText,
            MessagePanelEnvironmentModel(conversationUuid = conversationUuid),
            appContext,
            messagePanelUseCaseComponent
        )

    fun buildQuoteMessageUseCase(
        conversationUuid: UUID,
        quotingMessageUuid: UUID,
        quoteTitle: String,
        quoteText: String
    ): AbstractMessagePanelUseCase =
        QuoteMessageUseCase(
            quotingMessageUuid,
            quoteTitle,
            quoteText,
            MessagePanelEnvironmentModel(conversationUuid = conversationUuid),
            messagePanelUseCaseComponent
        )

    fun buildShareMessageUseCase(
        conversationUuid: UUID,
        sharedText: String?,
        sharedAttachments: List<String>?
    ): AbstractMessagePanelUseCase = ShareMessageUseCase(
        sharedText.orEmpty(),
        sharedAttachments?.map(Uri::parse) ?: emptyList(),
        MessagePanelEnvironmentModel(conversationUuid = conversationUuid)
    )
}
