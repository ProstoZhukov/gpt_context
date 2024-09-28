package ru.tensor.sbis.design.message_view.utils

import android.content.Context
import android.content.Intent
import io.reactivex.Observable
import ru.tensor.sbis.communication_decl.analytics.model.OpenUpdateAppLink
import ru.tensor.sbis.design.message_view.MessageViewPlugin.analyticsUtilFeatureProvider
import ru.tensor.sbis.design.message_view.MessageViewPlugin.docWebViewerFeatureProvider
import ru.tensor.sbis.design.message_view.MessageViewPlugin.openLinkControllerProvider
import ru.tensor.sbis.richtext.contract.DecoratedLinkOpenDependency
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreviewData
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController

/**
 * Реализация [DecoratedLinkOpenDependency] для предотвращения открытия экранов по клику по
 * линкам в богатом тексте, если в данный момент они не должны открываться,
 * например, в сценарии записи аудио/видеосообщения.
 *
 * @author vv.chekurda
 */
class MessageDecoratedLinkOpener : DecoratedLinkOpenDependency {

    private val controller by lazy { openLinkControllerProvider.get().openLinkController }
    private val docWebViewerFeature by lazy { docWebViewerFeatureProvider.get() }
    private val analyticsUtil by lazy { analyticsUtilFeatureProvider?.get()?.getAnalyticsUtil() }

    var checkLinkAvailability: (() -> Boolean)? = null

    override val openLinkController: OpenLinkController = object : OpenLinkController {
        override fun processAndForget(intent: Intent, isOuter: Boolean): Boolean =
            if (checkLinkAvailability?.invoke() != false) {
                controller.processAndForget(intent, isOuter)
            } else {
                true
            }

        override fun processAndForget(uri: String): Boolean =
            if (checkLinkAvailability?.invoke() != false) {
                sendAnalyticOnOpenUpdateAppLink(uri)
                controller.processAndForget(uri)
            } else {
                true
            }

        override fun processAndForget(link: LinkPreview) {
            if (checkLinkAvailability?.invoke() == false) return
            controller.processAndForget(link)
        }

        override fun processAndForget(data: LinkPreviewData) {
            if (checkLinkAvailability?.invoke() == false) return
            controller.processAndForget(data)
        }

        override fun process(intent: Intent, ignorePredictable: Boolean): Observable<Boolean> =
            if (checkLinkAvailability?.invoke() != false) {
                controller.process(intent, ignorePredictable)
            } else {
                Observable.empty()
            }

        override fun process(uri: String, ignorePredictable: Boolean): Observable<Boolean> =
            if (checkLinkAvailability?.invoke() != false) {
                controller.process(uri, ignorePredictable)
            } else {
                Observable.empty()
            }

        override fun processYourself(intent: Intent, ignorePredictable: Boolean): Observable<LinkPreview> =
            if (checkLinkAvailability?.invoke() != false) {
                controller.processYourself(intent, ignorePredictable)
            } else {
                Observable.empty()
            }

        override fun processYourself(uri: String, ignorePredictable: Boolean): Observable<LinkPreview> =
            if (checkLinkAvailability?.invoke() != false) {
                controller.processYourself(uri, ignorePredictable)
            } else {
                Observable.empty()
            }
    }

    override fun showDocumentLink(context: Context, title: String?, url: String) {
        if (checkLinkAvailability?.invoke() == false) return
        docWebViewerFeature.showDocumentLink(context, title, url)
    }

    override fun showDocumentLink(context: Context, title: String?, url: String, uuid: String?) {
        if (checkLinkAvailability?.invoke() == false) return
        docWebViewerFeature.showDocumentLink(context, title, url, uuid)
    }

    override fun createDocumentActivityIntent(context: Context, title: String?, url: String, uuid: String?): Intent =
        if (checkLinkAvailability?.invoke() != false) {
            docWebViewerFeature.createDocumentActivityIntent(context, title, url, uuid)
        } else {
            Intent()
        }

    private fun sendAnalyticOnOpenUpdateAppLink(url: String) {
        if (url.contains(PLAY_MARKET_LINK_PATH) && url.contains(TENSOR_APP_SUFFIX)) {
            analyticsUtil?.sendAnalytics(
                OpenUpdateAppLink(
                    MessageDecoratedLinkOpener::class.java.simpleName
                )
            )
        }
    }
}

private const val PLAY_MARKET_LINK_PATH = "play.google.com/store"
private const val TENSOR_APP_SUFFIX = "ru.tensor.sbis"