package ru.tensor.sbis.design.message_panel.decl.draft

import ru.tensor.sbis.design.message_panel.decl.MessagePanelUseCase
import java.util.*

/**
 * Интерфейс взаимодействия с сервисом черновиков
 *
 * @author ma.kolpakov
 */
interface MessageDraftService<out DRAFT> {

    suspend fun load(useCase: MessagePanelUseCase): DRAFT

    suspend fun save(
        useCase: MessagePanelUseCase,
        draftUuid: UUID,
        recipients: List<UUID>,
        text: String,
        attachments: List<UUID>
    )

    /**
     * Сохранение черновика для получателя. Одновременно может быть не более одного такого
     * черновика, при попытке сохранить черновик для другого пользователя, предыдущий черновик будет
     * потерян
     */
    suspend fun save(
        useCase: MessagePanelUseCase,
        draftUuid: UUID,
        recipient: UUID,
        text: String,
        attachments: List<UUID>
    )
}
