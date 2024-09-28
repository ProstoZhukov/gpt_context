package ru.tensor.sbis.docwebviewer.contract

import android.content.Context
import ru.tensor.sbis.edo_decl.document.DocWebViewerFeature
import ru.tensor.sbis.toolbox_decl.linkopener.builder.LinkOpenHandlerCreator
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandler
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenHandlerPriority
import ru.tensor.sbis.webviewer.contract.WebViewerFeature

/**
 * Имплементация интерфейса [DocWebViewerFeature] для DocWebViewer
 *
 * @author ma.kolpakov
 */
internal class DocWebViewerFeatureImpl(
    private val linkOpenerHandlerCreatorProvider: LinkOpenHandlerCreator.Provider,
    private val webViewerProvider: WebViewerFeature.Provider
) : DocWebViewerFeature, LinkOpenHandler.Provider {

    override fun showDocumentLink(context: Context, title: String?, url: String) {
        showDocumentLink(context, title, url, null)
    }

    override fun showDocumentLink(context: Context, title: String?, url: String, uuid: String?) {
        context.startActivity(createDocumentActivityIntent(context, title, url, uuid))
    }

    override fun createDocumentActivityIntent(context: Context, title: String?, url: String, uuid: String?) =
        webViewerProvider.webViewerFeature.getDocumentViewerActivityIntent(context, title, url, uuid)

    /** [LinkOpenHandler.Provider] */
    override fun getLinkOpenHandler(): LinkOpenHandler =
        linkOpenerHandlerCreatorProvider.linkOpenerHandlerCreator.create {
            on {
                type = DocType.TASK_FNS_REPORT
                priority(LinkOpenHandlerPriority.LOW) // Приоритет просмотра документа в WebView
                accomplishStart { preview, context ->
                    createDocumentActivityIntent(context, preview.title, preview.href, preview.docUuid)
                }
            }
            on {
                types(DocType.DOCUMENT, DocType.TRADES)
                subtypes(
                    LinkDocSubtype.TASK_WORK_PLAN,
                    LinkDocSubtype.TASK_WORK_PLAN_ITEM,
                    LinkDocSubtype.TASK_ORDER,
                    LinkDocSubtype.TASK_REQUIREMENTS,
                    LinkDocSubtype.REVIEW_ITEM,
                    LinkDocSubtype.RECLAMATION,
                    LinkDocSubtype.CHECKLIST_ITEM,
                    LinkDocSubtype.TASK_FNS_REPORT
                )
                priority(LinkOpenHandlerPriority.LOW) // Приоритет просмотра документа в WebView
                accomplishStart { preview, context ->
                    createDocumentActivityIntent(context, preview.title, preview.href, preview.docUuid)
                }
            }
        }
}