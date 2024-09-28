package ru.tensor.sbis.link_opener.domain.builder

import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.builder.LinkOpenEventBuilder
import ru.tensor.sbis.toolbox_decl.linkopener.builder.LinkOpenHandlerBuilder
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandler
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandler.Companion.MID_PRIORITY
import ru.tensor.sbis.link_opener.domain.handler.LinkOpenEventHandlerImpl
import ru.tensor.sbis.link_opener.domain.handler.LinkOpenHandlerImpl
import ru.tensor.sbis.link_opener.domain.utils.OnDocumentOpenListenerCreator
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandlerPriority

/**
 * Реализация билдера роутинга компонента открытия ссылок.
 *
 * @property openLinkEventHandlers поддерживаемые события открытия ссылок на документы.
 * @property defaultAction обработчик по-умолчанию, используется для документов не поддерживающих роутинг
 * в приложении, обычно требуется открытие документа в web view.
 * @property priorityLevel приоритет текущего обработчика по отношению к обработчикам
 * сторонних фич. [LinkOpenHandler.getPriority]
 *
 * @author as.chadov
 */
internal class LinkOpenHandlerBuilderImpl :
    BaseLinkOpenDslBuilder<LinkOpenHandler>,
    LinkOpenHandlerBuilder {

    private val openLinkEventHandlers = mutableListOf<LinkOpenEventHandlerImpl>()
    private var defaultAction: ((LinkPreview) -> Unit)? = null
    private var priorityLevel: LinkOpenHandlerPriority = LinkOpenHandlerPriority.NORMAL

    override fun on(init: LinkOpenEventBuilder.() -> Unit) {
        openLinkEventHandlers.add(LinkOpenEventBuilderImpl().apply(init).build())
    }

    override fun default(handler: (LinkPreview) -> Unit) {
        defaultAction = handler
    }

    override fun priority(level: Int) {
        priorityLevel = when {
            level == MID_PRIORITY -> LinkOpenHandlerPriority.NORMAL
            level > MID_PRIORITY  -> LinkOpenHandlerPriority.HIGH
            else                  -> LinkOpenHandlerPriority.LOW
        }
    }

    override fun priority(level: LinkOpenHandlerPriority) {
        priorityLevel = level
    }

    override fun build(): LinkOpenHandler {
        // дублируем приоритет прикладного обработчика на обработчики событий в нем
        openLinkEventHandlers.forEach { it.priority = priorityLevel }
        val defaultHandler = defaultAction?.let { OnDocumentOpenListenerCreator.create(it) }
        return LinkOpenHandlerImpl(
            eventHandlers = openLinkEventHandlers,
            defaultAction = defaultHandler,
            priorityLevel = priorityLevel
        )
    }

    companion object {
        /**
         * Метод декларативного объявления роутера открытия документов по ссылкам для поддержки навигации.
         */
        fun create(init: LinkOpenHandlerBuilder.() -> Unit) =
            LinkOpenHandlerBuilderImpl().run {
                init()
                build()
            }

        /**
         * Метод декларативного объявления роутера открытия документов по ссылкам для поддержки навигации.
         */
        @JvmSynthetic
        operator fun invoke(init: LinkOpenHandlerBuilder.() -> Unit) =
            create(init)
    }
}
