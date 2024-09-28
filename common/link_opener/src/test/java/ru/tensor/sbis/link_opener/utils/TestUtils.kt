package ru.tensor.sbis.link_opener.utils

import ru.tensor.sbis.link_opener.domain.handler.LinkOpenEventHandlerImpl
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandler
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandlerPriority

internal fun getEventHandler(
    type: DocType = DocType.UNKNOWN,
    subtype: LinkDocSubtype = LinkDocSubtype.UNKNOWN,
    priority: LinkOpenHandlerPriority = LinkOpenHandlerPriority.NORMAL
) = LinkOpenEventHandlerImpl(
    types = listOf(type),
    subtypes = listOf(subtype),
    action = null,
    actionRouter = null,
    priority = priority,
)