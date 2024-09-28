package ru.tensor.sbis.link_opener.domain.router.producer

import ru.tensor.sbis.edo_decl.document.DocWebViewerFeature
import ru.tensor.sbis.link_opener.contract.LinkOpenerDependency
import ru.tensor.sbis.link_opener.domain.handler.LinkOpenEventHandlerImpl
import ru.tensor.sbis.link_opener.domain.router.LinkOpenHandlerFactory
import ru.tensor.sbis.link_opener.domain.utils.OnDocumentOpenListenerCreator
import ru.tensor.sbis.link_opener.domain.utils.OpenLinkActionWithContext
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenEventHandler
import javax.inject.Inject

/**
 * Производитель обработчиков для СБИС ссылок.
 * Ссылка будет открыта в WebView.
 * Используется если прикладных специфичных и по умолчанию обработчиков не было найдено, но ссылка "наша", т.е. известен
 * тип документа или домен ресурса.
 *
 * @param dependency зависимость с [DocWebViewerFeature] фичей для просмотра документов без
 * зарегистрированных обработчиков в WebView.
 *
 * @author as.chadov
 */
internal class OurLinkOpenHandlerProducer @Inject constructor(
    private val dependency: LinkOpenerDependency
) : LinkOpenHandlerFactory.LinkOpenHandlerProducer {

    override fun produce(preview: LinkPreview): LinkOpenEventHandler =
        LinkOpenEventHandlerImpl(
            types = listOf(DocType.UNKNOWN),
            subtypes = listOf(LinkDocSubtype.UNKNOWN),
            action = OnDocumentOpenListenerCreator.create(createAction())
        )

    private fun createAction(): OpenLinkActionWithContext = { preview, context ->
        dependency.showDocumentLink(
            context = context,
            title = preview.title,
            url = preview.href,
            uuid = preview.docUuid.takeIf(String::isNotBlank)
        )
    }
}
