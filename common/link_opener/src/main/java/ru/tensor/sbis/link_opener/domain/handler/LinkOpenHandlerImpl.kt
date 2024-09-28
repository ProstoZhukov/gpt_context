package ru.tensor.sbis.link_opener.domain.handler

import ru.tensor.sbis.toolbox_decl.linkopener.action.OnDocumentOpenListener
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenEventHandler
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandler
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandlerPriority

/**
 * Реализация для прикладного обработчика открытия документов по ссылкам.
 *
 * @param eventHandlers поддерживаемые обработчики событий открытия ссылок на документы.
 * @param defaultAction обработчик открытия ссылок по-умолчанию.
 * @param priorityLevel Приоритет текущего обработчика по отношению к обработчикам
 * сторонних фич. [LinkOpenHandler.getPriority].
 *
 * @author as.chadov
 */
internal class LinkOpenHandlerImpl(
    private var eventHandlers: List<LinkOpenEventHandlerImpl>,
    private val defaultAction: OnDocumentOpenListener?,
    private val priorityLevel: LinkOpenHandlerPriority
) : LinkOpenHandler {

    override fun getEventHandlers(): List<LinkOpenEventHandler> =
        eventHandlers

    override fun getDefaultHandler(): OnDocumentOpenListener? =
        defaultAction

    override fun getPriority(): LinkOpenHandlerPriority = priorityLevel

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LinkOpenHandlerImpl

        if (eventHandlers.distinct() != other.eventHandlers.distinct()) return false
        if (priorityLevel != other.priorityLevel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = priorityLevel.hashCode()
        result = 31 * result + eventHandlers.hashCode()
        return result
    }

    /** @SelfDocumented */
    fun getDefaultEventHandler(): LinkOpenEventHandlerImpl? =
        takeIf { defaultAction != null }
            ?.run {
                LinkOpenEventHandlerImpl(
                    types = listOf(DocType.UNKNOWN),
                    subtypes = listOf(LinkDocSubtype.UNKNOWN),
                    action = defaultAction,
                    priority = priorityLevel
                )
            }
}
