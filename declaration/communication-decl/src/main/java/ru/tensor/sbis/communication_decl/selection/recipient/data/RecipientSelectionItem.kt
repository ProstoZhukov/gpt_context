package ru.tensor.sbis.communication_decl.selection.recipient.data

import java.util.UUID

/**
 * Интерфейс элемента выбора получателей.
 *
 * @author vv.chekurda
 */
interface RecipientSelectionItem {

    /**
     * Идентификатор элемента.
     */
    val uuid: UUID

    /**
     * Заголовок элемента.
     */
    val title: String
}