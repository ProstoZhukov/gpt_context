package ru.tensor.sbis.link_opener.domain.router.producer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Browser
import androidx.core.app.BundleCompat
import ru.tensor.sbis.link_opener.domain.handler.LinkOpenEventHandlerImpl
import ru.tensor.sbis.link_opener.domain.router.LinkOpenHandlerFactory
import ru.tensor.sbis.link_opener.domain.utils.OnDocumentOpenListenerCreator
import ru.tensor.sbis.link_opener.domain.utils.OpenLinkActionWithContext
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenEventHandler
import timber.log.Timber
import java.net.URI
import javax.inject.Inject

/**
 * Производитель обработчиков для НЕ СБИС ссылок.
 * Ссылка будет открыта браузером.
 * Используется если прикладных специфичных и по умолчанию обработчиков не было найдено и ссылка
 * на неизвестный МП ресурс.
 *
 * @author as.chadov
 */
internal class ForeignLinkOpenHandlerProducer @Inject constructor() :
    LinkOpenHandlerFactory.LinkOpenHandlerProducer {

    override fun produce(preview: LinkPreview): LinkOpenEventHandler =
        LinkOpenEventHandlerImpl(
            types = listOf(DocType.UNKNOWN),
            subtypes = listOf(LinkDocSubtype.UNKNOWN),
            action = OnDocumentOpenListenerCreator.create(createAction())
        )

    private fun createAction(): OpenLinkActionWithContext = fun(preview, context) {
        if (preview.href.isBlank()) {
            Timber.e("Not found url to open in web view")
            return
        }
        try {
            Timber.d("Handle external link action with href: ${preview.href}")
            var uri = URI(preview.href).normalize()
            if (uri.scheme.isNullOrBlank()) {
                uri = URI("$HTTP${preview.href}")
            }
            val linkIntent = buildIntent(context, uri)
            context.startActivity(linkIntent)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun buildIntent(context: Context, uri: URI): Intent =
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(uri.toASCIIString())
            Timber.d("Start browser activity with data: $data")
            setSessionParameters(this)
            putExtra(Browser.EXTRA_APPLICATION_ID, context.packageName)
            addCategory(Intent.CATEGORY_BROWSABLE)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

    /**
     * Взято из androidx.browser.customTabs.CustomTabsIntent.
     * Без этого браузер chrome на xiaomi, huawei не открывает кириллические домены.
     */
    @Suppress("SpellCheckingInspection")
    private fun setSessionParameters(intent: Intent) {
        val bundle = Bundle()
        BundleCompat.putBinder(bundle, "android.support.customtabs.extra.SESSION", null)
        intent.putExtras(bundle)
    }

    private companion object {
        const val HTTP = "http://"
    }
}
