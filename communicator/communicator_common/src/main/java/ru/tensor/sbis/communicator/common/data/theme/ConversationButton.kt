package ru.tensor.sbis.communicator.common.data.theme

import java.util.UUID

/**
 * Модель кнопки ячейки диалога/канала.
 *
 * @author da.zhukov
 */
data class ConversationButton(
    val messageUUID: UUID,
    val buttonId: String,
    val title: String,
    val link: String?,
    val isActive: Boolean,
    val isOutlineMode: Boolean
)