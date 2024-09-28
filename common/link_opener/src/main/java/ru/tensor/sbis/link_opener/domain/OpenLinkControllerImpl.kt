package ru.tensor.sbis.link_opener.domain

import android.content.Intent
import dagger.Lazy
import io.reactivex.Observable
import ru.tensor.sbis.link_opener.data.UriContainer
import ru.tensor.sbis.link_opener.data.UriContainer.Companion.IS_OUTER_LINK_KEY
import ru.tensor.sbis.link_opener.data.isRedirectable
import ru.tensor.sbis.link_opener.domain.parser.DeeplinkParser
import ru.tensor.sbis.link_opener.domain.router.LinkOpenerRouter
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreviewData
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Реализация [OpenLinkController].
 *
 * @param parser парсер намерения интент-фильтра в превью ссылки на документ
 * @param router роутер навигации ссылок к экранам приложения
 *
 * @author as.chadov
 */
@Singleton
internal class OpenLinkControllerImpl @Inject constructor(
    parser: Lazy<DeeplinkParser>,
    router: Lazy<LinkOpenerRouter>
) : OpenLinkController {

    private val parser: DeeplinkParser by lazy { parser.get() }
    private val router: LinkOpenerRouter by lazy { router.get() }

    override fun processAndForget(intent: Intent, isOuter: Boolean): Boolean {
        intent.putExtra(IS_OUTER_LINK_KEY, isOuter)
        return processIntentOrUriAndForget(intent = intent)
    }

    override fun processAndForget(uri: String): Boolean =
        processIntentOrUriAndForget(uri = uri)

    override fun processAndForget(link: LinkPreview) =
        parser.executePostParsing(link, ::routeLink)

    override fun processAndForget(data: LinkPreviewData) =
        processAndForget(data.model)

    override fun process(
        intent: Intent,
        ignorePredictable: Boolean
    ): Observable<Boolean> = processIntentOrUri(
        intent = intent,
        ignorePredictable = ignorePredictable
    )

    override fun process(
        uri: String,
        ignorePredictable: Boolean
    ): Observable<Boolean> = processIntentOrUri(
        uri = uri,
        ignorePredictable = ignorePredictable
    )

    override fun processYourself(
        intent: Intent,
        ignorePredictable: Boolean
    ): Observable<LinkPreview> = processIntentOrUriYourself(
        intent = intent,
        ignorePredictable = ignorePredictable
    )

    override fun processYourself(
        uri: String,
        ignorePredictable: Boolean
    ): Observable<LinkPreview> = processIntentOrUriYourself(
        uri = uri,
        ignorePredictable = ignorePredictable
    )

    private fun processIntentOrUriAndForget(
        intent: Intent? = null,
        uri: String = ""
    ): Boolean = parser.executeOnParsing(
        args = UriContainer(intent, uri),
        onParse = ::routeLink
    )

    private fun routeLink(link: LinkPreview) {
        if (link.isRedirectable()) {
            router.navigate(link).take(1).subscribe()
        }
    }

    private fun processIntentOrUriYourself(
        intent: Intent? = null,
        uri: String = "",
        ignorePredictable: Boolean
    ): Observable<LinkPreview> = parser.observeParsing(UriContainer(intent, uri))
        .filter { !ignorePredictable || !it.isPredictable }
        .cast(LinkPreview::class.java)

    private fun processIntentOrUri(
        intent: Intent? = null,
        uri: String = "",
        ignorePredictable: Boolean
    ): Observable<Boolean> = parser.observeParsing(UriContainer(intent, uri))
        .filter { !ignorePredictable || !it.isPredictable }
        .switchMap { link ->
            if (link.isRedirectable()) {
                router.navigate(link)
            } else {
                Observable.just(false)
            }
        }
}
