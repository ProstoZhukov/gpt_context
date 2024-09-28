package ru.tensor.sbis.link_opener.domain.handler

import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.action.OnDocumentIntentListener
import ru.tensor.sbis.toolbox_decl.linkopener.action.OnDocumentOpenListener
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenEventHandler
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandler
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandlerPriority

/**
 * Реализация обработчика открытия ссылки конкретного типа [LinkOpenEventHandler].
 *
 * @param types [LinkOpenEventHandler.types]
 * @param subtypes [LinkOpenEventHandler.subtypes]
 * @param action [LinkOpenEventHandler.action]
 * @param actionRouter [LinkOpenEventHandler.actionRouter]
 * @param priority [LinkOpenEventHandler.priority]
 * Устанавливается из [LinkOpenHandler.getPriority].
 *
 * @author as.chadov
 */
internal data class LinkOpenEventHandlerImpl constructor(
    override var types: List<DocType>,
    override var subtypes: List<LinkDocSubtype> = listOf(),
    override var action: OnDocumentOpenListener? = null,
    override var actionRouter: OnDocumentIntentListener? = null,
    override var priority: LinkOpenHandlerPriority = LinkOpenHandlerPriority.NORMAL
) : LinkOpenEventHandler {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LinkOpenEventHandlerImpl

        if (types != other.types) return false
        if (subtypes != other.subtypes) return false
        if (priority != other.priority) return false

        return true
    }

    override fun hashCode(): Int {
        var result = types.hashCode()
        result = 31 * result + subtypes.hashCode()
        result = 31 * result + priority.ordinal
        return result
    }

    /**
     * Возвращает true если текущий [LinkOpenEventHandler] подходит для обработки [preview].
     *
     * @param explicitly true если требуется явна обработка по типу и подтипу документа.
     */
    fun canHandle(preview: LinkPreview, explicitly: Boolean = false): Boolean {
        val isTargetSubtype = if (explicitly) {
            subtypes.contains(preview.docSubtype)
        } else {
            subtypes.isEmpty() || subtypes.contains(preview.docSubtype)
        }
        return types.contains(preview.docType) && isTargetSubtype
    }
}
