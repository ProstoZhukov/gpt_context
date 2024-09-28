package ru.tensor.sbis.link_opener.domain.router.producer

import android.content.Intent
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import ru.tensor.sbis.link_opener.di.CUSTOM_TABS_COLOR
import ru.tensor.sbis.link_opener.domain.handler.LinkOpenEventHandlerImpl
import ru.tensor.sbis.link_opener.domain.router.LinkOpenHandlerFactory
import ru.tensor.sbis.link_opener.domain.utils.OnDocumentOpenListenerCreator
import ru.tensor.sbis.link_opener.domain.utils.OpenLinkActionWithContext
import ru.tensor.sbis.toolbox_decl.linkopener.LinkPreview
import ru.tensor.sbis.toolbox_decl.linkopener.data.DocType
import ru.tensor.sbis.toolbox_decl.linkopener.data.LinkDocSubtype
import ru.tensor.sbis.toolbox_decl.linkopener.handler.LinkOpenEventHandler
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

/**
 * Производитель обработчиков для НЕ СБИС ссылок.
 * Ссылка будет открыта Chrome custom tabs.
 * Используется если разрешено настройками и есть поддерживаемые браузеры на устройстве.
 *
 * @param toolbarColor цвет заливки тулбара.
 *
 * @author as.chadov
 */
internal class TabsLinkOpenHandlerProducer @Inject constructor(
    @Named(CUSTOM_TABS_COLOR) @ColorInt private val toolbarColor: Int
) : LinkOpenHandlerFactory.LinkOpenHandlerProducer {

    override fun produce(preview: LinkPreview): LinkOpenEventHandler =
        LinkOpenEventHandlerImpl(
            types = listOf(DocType.UNKNOWN),
            subtypes = listOf(LinkDocSubtype.UNKNOWN),
            action = OnDocumentOpenListenerCreator.create(createAction())
        )

    private fun createAction(): OpenLinkActionWithContext = fun(preview, context) {
        if (preview.href.isBlank()) {
            Timber.e("Not found url to open in custom tabs")
            return
        }
        try {
            Timber.d("Handle external link action with custom tabs: ${preview.href}")
            var uri = Uri.parse(preview.href).normalizeScheme()
            if (uri.scheme.isNullOrBlank()) {
                uri = Uri.parse("$HTTP${preview.href}")
            }
            buildIntent().launchUrl(context, uri)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun buildIntent(): CustomTabsIntent {
        val toolbarSettings = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(toolbarColor)
            .build()
        return CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(toolbarSettings)
            .build().apply {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
    }

    private companion object {
        const val HTTP = "http://"
    }
}