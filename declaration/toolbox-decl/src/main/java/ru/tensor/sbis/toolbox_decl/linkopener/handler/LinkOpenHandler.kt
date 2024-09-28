package ru.tensor.sbis.toolbox_decl.linkopener.handler

import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.toolbox_decl.linkopener.action.OnDocumentOpenListener
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype

/**
 * Интерфейс обработчика открытия контента (документов) по ссылкам прикладным функционалом (модулем, фичей).
 *
 * @author as.chadov
 */
interface LinkOpenHandler {

    /** Возвращает список обработчиков событий открытия ссылок на документы поддерживаемых прикладным функционалом (модулем, фичей). */
    fun getEventHandlers(): List<LinkOpenEventHandler>

    /** Возвращает опциональный обработчик по-умолчанию для события открытия ссылки на неизвестный контент для прикладного функционала. */
    fun getDefaultHandler(): OnDocumentOpenListener? = null

    /**
     * Приоритет текущего обработчика по отношению к обработчикам сторонних фич, по умолчанию [Priority.NORMAL].
     * Используется при определении обработчика которому будет делегирован вызов если на один набор типов [DocType] и [LinkDocSubtype]
     * таких зарегистрировано несколько.
     * Приоритет обработчика [LinkOpenHandler] разворачивается на все его производные [LinkOpenEventHandler].
     */
    fun getPriority(): LinkOpenHandlerPriority = LinkOpenHandlerPriority.NORMAL

    /**
     * Поставщик реализации открытия документов по ссылкам [LinkOpenHandler].
     * Должен наследоваться/реализовываться Feature интерфейсом предоставляющим доступ к функционалу прикладной области/модуля.
     */
    interface Provider : Feature {
        fun getLinkOpenHandler(): LinkOpenHandler
    }

    companion object {
        /** Приоритет обработчика по умолчанию. */
        @Deprecated("Устаревший подход", ReplaceWith("LinkOpenHandler.Priority"))
        const val MID_PRIORITY = 2
        internal const val MIN_PRIORITY = 0L
        internal const val MAX_PRIORITY = 4L
    }
}