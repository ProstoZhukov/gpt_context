package ru.tensor.sbis.link_opener.domain.router

import ru.tensor.sbis.link_opener.domain.handler.LinkOpenEventHandlerImpl
import ru.tensor.sbis.link_opener.domain.handler.LinkOpenHandlerImpl
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandler
import timber.log.Timber
import java.util.Collections
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Холдер прикладных обработчиков роутинга ссылок.
 *
 * @property handlers зарегистрированные прикладные обработчики открытия контента/документов.
 *
 * @author as.chadov
 */
@Singleton
internal class LinkHandlersHolder @Inject constructor() {

    private val handlers = Collections.synchronizedList<LinkOpenHandler>(mutableListOf())

    /**
     * Добавить прикладной обработчик.
     * Если такой же обработчик уже существует, то он замещается новым, иначе добавляется в конец,
     * сравнение происходит по [LinkOpenHandlerImpl.equals].
     */
    fun addHandler(vararg newHandlers: LinkOpenHandler) = synchronized(handlers) {
        try {
            val iterator = newHandlers.iterator()
            while (iterator.hasNext()) {
                val newHandler = iterator.next()
                val insertIndex = handlers.findLast { it == newHandler }?.let(handlers::indexOf)
                if (insertIndex == null) {
                    handlers.add(newHandler)
                } else {
                    handlers[insertIndex] = newHandler
                }
            }
        } catch (ex: ConcurrentModificationException) {
            Timber.e(ex)
        }
    }

    /**
     * Удалить прикладной обработчик.
     */
    fun removeHandler(vararg handler: LinkOpenHandler): Unit = synchronized(handlers) {
        // .toSet() - подсказка от lint для улучшения performance
        handlers.removeAll(handler.toSet())
    }

    /**
     * Получить все зарегистрированные обработчики по типам ссылок.
     */
    fun getAllHandlers(): List<LinkOpenEventHandlerImpl> = synchronized(handlers) {
        handlers
            .flatMap(LinkOpenHandler::getEventHandlers)
            .filterIsInstance<LinkOpenEventHandlerImpl>()
    }

    /**
     * Получить все зарегистрированные обработчики по типам ссылок по умолчанию.
     */
    fun getDefaultHandlers(): List<LinkOpenEventHandlerImpl> = synchronized(handlers) {
        handlers
            .filterIsInstance<LinkOpenHandlerImpl>()
            .mapNotNull(LinkOpenHandlerImpl::getDefaultEventHandler)
    }
}