package ru.tensor.sbis.link_opener.domain.parser

import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import ru.tensor.sbis.toolbox_decl.linkopener.service.LinkDecoratorServiceRepository
import ru.tensor.sbis.link_opener.data.InnerLinkPreview
import javax.inject.Inject

/**
 * Расширяемый парсер (пост обработчик [DeeplinkParser]).
 * Парсер может дополняться прикладными разработчиками, если микросервис контроллера
 * [LinkDecoratorServiceRepository] не работает с желаемым [DocType].
 *
 * ВАЖНО: делегирование обработки ссылки к [ScalableParser] является временным решением.
 * Поддержка должна осуществляться на стороне микросервиса контроллера, подробнее смотреть README
 * компонента п. "Особенности работы".
 *
 * @author as.chadov
 */
internal open class ScalableParser @Inject constructor() {
    /**
     * Обработка НЕ СБИС ссылки.
     */
    fun parseNoSbisUrlToLinkPreview(link: String): InnerLinkPreview =
        InnerLinkPreview(link).also { evaluateUnknownDocType(it) }

    /**
     * Пост обработка превью ссылки с типом [DocType.UNKNOWN].
     * @param linkPreview превью ссылки.
     */
    fun <T : LinkPreview> postProcessUnknownLinkPreview(linkPreview: T): T =
        evaluateUnknownDocType(linkPreview)

    /**
     * Обработка данные о ссылке на стороне МП.
     * @param linkPreview превью ссылки с типом [DocType.UNKNOWN].
     *
     * Важно: любая БЛ в данном методе без ссылки на задачу доработки микросервиса мобильной платформой может быть удалена.
     */
    private fun <T : LinkPreview> evaluateUnknownDocType(linkPreview: T): T {
        if (linkPreview.docType != DocType.UNKNOWN) {
            return linkPreview
        }
        val url = linkPreview.href
        when {
            VIDEOCALL in url -> linkPreview.docType = DocType.VIDEOCALL
            ONLINE in url -> linkPreview.docType = DocType.UNKNOWN_ONLINE_DOC
            else          -> Unit
        }
        return linkPreview
    }

    private companion object Path {
        const val VIDEOCALL = "/meet/"
        const val ONLINE = "online.sbis.ru"
    }
}
