package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.utils

import ru.tensor.sbis.communicator.generated.DialogFilter
import ru.tensor.sbis.communicator.declaration.model.DialogType

/**
 * Маппер типа диалога в тип фильтра
 */
internal fun DialogType.toDialogFilter(): DialogFilter = when (this) {
    DialogType.ALL        -> DialogFilter.ALL
    DialogType.INCOMING   -> DialogFilter.INCOMING
    DialogType.UNREAD     -> DialogFilter.UNREAD
    DialogType.UNANSWERED -> DialogFilter.UNANSWERED
    DialogType.DELETED    -> DialogFilter.DELETED
}
