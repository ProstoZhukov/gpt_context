package ru.tensor.sbis.webviewer.contract

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Feature модуля WebViewer
 *
 * @author ma.kolpakov
 */
interface WebViewerFeature : Feature {

    fun getDocumentViewerActivityIntent(context: Context, title: String?, url: String, uuid: String?): Intent

    /** Открытие WebViewer без тулбара */
    fun getDocumentViewerActivityIntentNoToolbar(context: Context, url: String): Intent

    /**
     * Провайдер фичи [WebViewerFeature]
     */
    interface Provider : Feature {
        val webViewerFeature: WebViewerFeature
    }
}