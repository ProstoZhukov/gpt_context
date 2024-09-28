package ru.tensor.sbis.communication_decl.recipient_selection

import java.util.UUID

/**
 * Контракт результата выбора сотрудников
 */
interface RecipientSelectionResultDataContract {
    /**
     * Получить выбранные идентификаторы
     */
    fun getContactUuids(): List<UUID>
}