package ru.tensor.sbis.toolbox_decl.linkopener.handler

import ru.tensor.sbis.toolbox_decl.linkopener.action.OnDocumentIntentListener
import ru.tensor.sbis.toolbox_decl.linkopener.action.OnDocumentOpenListener
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype

/**
 * Интерфейс обработчика открытия ссылки конкретного типа в [LinkOpenHandler].
 *
 * @author as.chadov
 */
interface LinkOpenEventHandler {

    /** Обрабатываемый тип(ы) ссылок. */
    var types: List<DocType>

    /** Обрабатываемый подтип(ы) ссылок. */
    var subtypes: List<LinkDocSubtype>

    /** Действие по открытию контента/документа по ссылкам типов [types]. */
    var action: OnDocumentOpenListener?

    /** Действие роутера приложения [RouterInterface] по открытию документа по ссылкам типов [types]. */
    var actionRouter: OnDocumentIntentListener?

    /** Приоритет текущего обработчика по отношению к обработчикам сторонних фич. */
    val priority: LinkOpenHandlerPriority
}