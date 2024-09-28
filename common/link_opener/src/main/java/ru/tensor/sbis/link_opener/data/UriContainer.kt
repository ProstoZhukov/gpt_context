package ru.tensor.sbis.link_opener.data

import android.content.Intent
import android.net.Uri
import ru.tensor.sbis.toolbox_decl.linkopener.OpenLinkController.Companion.FORCED_NAVIGATION_TO_WEBVIEW

/**
 * Контейнер с входными данными цифрового ресурса.
 *
 * @param intent ожидаемый интент открытия документа.
 * @param uri опциональный [Uri] ожидаемого интента открытия документа.
 */
internal class UriContainer(
    val intent: Intent? = null,
    val uri: String = "",
) {
    /** true если источник ссылки интент [Intent]. */
    val isIntentSource = intent != null

    /** true если ссылка получена извне через интент инициатор запуска МП. */
    val isOuterLink = intent?.getBooleanExtra(IS_OUTER_LINK_KEY, false) ?: false

    /** true если ссылка должна быть обязательно открыта в WebView. */
    val isWebViewVisitor = intent?.getBooleanExtra(FORCED_NAVIGATION_TO_WEBVIEW, false) ?: false

    /** @SelfDocumented */
    val uriString: String = if (isIntentSource) {
        intent?.data?.toString().orEmpty()
    } else {
        uri
    }

    companion object {
        const val IS_OUTER_LINK_KEY = "is_outer_link"
    }
}