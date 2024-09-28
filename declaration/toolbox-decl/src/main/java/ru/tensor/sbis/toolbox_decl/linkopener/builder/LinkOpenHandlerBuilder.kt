package ru.tensor.sbis.toolbox_decl.linkopener.builder

import androidx.annotation.IntRange
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandler
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandler.Companion.MAX_PRIORITY
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandler.Companion.MIN_PRIORITY
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandlerPriority

/**
 * Интерфейс билдера прикладного обработчика открытия ссылок [LinkOpenHandler].
 *
 * @author as.chadov
 */
interface LinkOpenHandlerBuilder {

    /**
     * Установить обработчик открытия ссылки специфического контента/документа.
     * [LinkOpenHandler.getEventHandlers].
     */
    fun on(init: LinkOpenEventBuilder.() -> Unit)

    /**
     * Установить обработчик по умолчанию открытия ссылки неопределенного контента/документа.
     * [LinkOpenHandler.getDefaultHandler].
     */
    fun default(handler: (LinkPreview) -> Unit)

    /**
     * Установить приоритет текущего обработчика по отношению к обработчикам сторонних фич.
     * Подробнее [LinkOpenHandler.getPriority].
     */
    @Deprecated("Устаревший подход", ReplaceWith("LinkOpenHandlerBuilder.priority"))
    fun priority(@IntRange(from = MIN_PRIORITY, to = MAX_PRIORITY) level: Int)

    /**
     * Установить приоритет текущего обработчика по отношению к обработчикам сторонних фич.
     * Обработчик ДОЛЖЕН объявлять низкий приоритет [LinkOpenHandler.Priority.LOW] если не в состоянии открыть документ по ссылке
     * на прикладной карточке (своем экране), а использует для этого например WebView или схожий функционал.
     * Подробнее [LinkOpenHandler.getPriority].
     */
    fun priority(level: LinkOpenHandlerPriority)
}