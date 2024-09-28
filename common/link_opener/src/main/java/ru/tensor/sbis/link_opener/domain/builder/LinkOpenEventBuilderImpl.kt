package ru.tensor.sbis.link_opener.domain.builder

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.action.OnDocumentIntentListener
import ru.tensor.sbis.toolbox_decl.linkopener.action.OnDocumentOpenListener
import ru.tensor.sbis.toolbox_decl.linkopener.builder.LinkOpenEventBuilder
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import ru.tensor.sbis.link_opener.domain.handler.LinkOpenEventHandlerImpl
import ru.tensor.sbis.link_opener.domain.router.LinkOpenerRouter
import ru.tensor.sbis.link_opener.domain.utils.OnDocumentOpenListenerCreator

/**
 * Билдер события открытия ссылки для роутера [LinkOpenerRouter].
 *
 * @property documentTypes поддерживаемые типы ссылок
 * @property documentSubtypes поддерживаемые подтипы ссылок
 * @property documentOpenAction действие при открытии ссылки документа
 * @property documentIntentAction действие получения намерения при открытии ссылки документа
 *
 * @author as.chadov
 */
internal class LinkOpenEventBuilderImpl :
    BaseLinkOpenDslBuilder<LinkOpenEventHandlerImpl>,
    LinkOpenEventBuilder {

    private var documentTypes = mutableListOf<DocType>()
    private var documentSubtypes = mutableListOf<LinkDocSubtype>()
    private var documentOpenAction: OnDocumentOpenListener? = null
    private var documentIntentAction: OnDocumentIntentListener? = null

    override var type: DocType = DocType.UNKNOWN
        set(value) {
            documentTypes.add(value)
        }

    override var subtype: LinkDocSubtype = LinkDocSubtype.UNKNOWN
        set(value) {
            documentSubtypes.add(value)
        }

    override fun types(vararg type: DocType) {
        documentTypes.addAll(type)
    }

    override fun subtypes(vararg types: LinkDocSubtype) {
        documentSubtypes.addAll(types)
    }

    override fun accomplish(action: (LinkPreview) -> Unit) {
        documentOpenAction = OnDocumentOpenListenerCreator.create(action)
    }

    override fun accomplish(action: (LinkPreview, Context) -> Unit) {
        documentOpenAction = OnDocumentOpenListenerCreator.create(action)
    }

    override fun accomplishStart(action: (LinkPreview) -> Intent?) {
        documentIntentAction = OnDocumentOpenListenerCreator.createIntent(action)
    }

    override fun accomplishStart(action: (LinkPreview, Context) -> Intent?) {
        documentIntentAction = OnDocumentOpenListenerCreator.createIntent(action)
    }

    override fun build(): LinkOpenEventHandlerImpl =
        if (documentTypes.isNotEmpty() &&
            (documentOpenAction != null || documentIntentAction != null)) {
            LinkOpenEventHandlerImpl(
                types = documentTypes,
                subtypes = documentSubtypes,
                action = documentOpenAction,
                actionRouter = documentIntentAction
            )
        } else {
            throw IllegalStateException("You cannot create LinkOpenEventHandler when actions" +
             " or types are not specified")
        }
}
