package ru.tensor.sbis.link_opener.domain.router.producer

import dagger.Lazy
import ru.tensor.sbis.link_opener.domain.handler.LinkOpenEventHandlerImpl
import ru.tensor.sbis.link_opener.domain.router.LinkHandlersHolder
import ru.tensor.sbis.link_opener.domain.router.LinkOpenHandlerFactory
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenEventHandler
import javax.inject.Inject

/**
 * Производитель зарегистрированных в текущем МП обработчиков.
 * Ссылка будет открыта на карточке одного из разделов текущего МП.
 *
 * @param linkHandlersHolder холдер прикладных обработчиков роутинга ссылок.
 *
 * @author as.chadov
 */
internal class InAppLinkOpenHandlerProducer @Inject constructor(
    private val linkHandlersHolder: Lazy<LinkHandlersHolder>
) : LinkOpenHandlerFactory.LinkOpenHandlerProducer {

    private val handlerMap: LinkHandlersHolder by lazy { linkHandlersHolder.get() }

    override fun produce(preview: LinkPreview): LinkOpenEventHandler? =
        findSpecificEventHandlers(preview)
            .takeIf(List<LinkOpenEventHandler>::isNotEmpty)?.firstOrNull()
            ?: findDefaultEventHandler()

    /**
     * Найти все специфичные обработчики для события открытия ссылки [link].
     * @param link данные о ссылке на контент/документ.
     *
     * @return все подходящие обработчики для [link] с сортировкой по приоритету (от высокоприоритетных ко низкоприоритетным).
     */
    private fun findSpecificEventHandlers(link: LinkPreview): List<LinkOpenEventHandler> {
        val allHandlers = handlerMap.getAllHandlers()
        val foundHandlers = allHandlers.filter { it.canHandle(link, true) }
        return foundHandlers.ifEmpty {
            allHandlers.filter { it.canHandle(link, false) }
        }.sortedByDescending(LinkOpenEventHandlerImpl::priority)
    }

    /**
     * Получить наиболее подходящий обработчик для событий по-умолчанию.
     */
    private fun findDefaultEventHandler(): LinkOpenEventHandler? =
        handlerMap.getDefaultHandlers().maxByOrNull(LinkOpenEventHandlerImpl::priority)
}