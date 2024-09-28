package ru.tensor.sbis.webviewer.contract

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.webviewer.DocumentViewerActivity

/**
 * Имплементация интерфейса [WebViewerFeature] для WebViewer
 *
 * @author ma.kolpakov
 */
internal class WebViewerFeatureImpl : WebViewerFeature {

    override fun getDocumentViewerActivityIntent(context: Context, title: String?, url: String, uuid: String?): Intent =
        Intent(context, DocumentViewerActivity::class.java).apply {
            putExtra(DocumentViewerActivity.EXTRA_DOCUMENT_TITLE, title)
            putExtra(DocumentViewerActivity.EXTRA_DOCUMENT_URL, url)
            putExtra(DocumentViewerActivity.EXTRA_DOCUMENT_ID, uuid)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

    override fun getDocumentViewerActivityIntentNoToolbar(context: Context, url: String): Intent =
        Intent(context, DocumentViewerActivity::class.java).apply {
            putExtra(DocumentViewerActivity.EXTRA_DOCUMENT_URL, url)
            putExtra(DocumentViewerActivity.EXTRA_HIDE_TOOLBAR, true)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
}