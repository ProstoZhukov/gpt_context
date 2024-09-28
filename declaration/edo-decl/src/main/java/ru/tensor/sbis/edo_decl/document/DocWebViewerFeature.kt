package ru.tensor.sbis.edo_decl.document

import android.content.Context
import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Просмотр документа в WebView (вынесено из common)
 */
/* интерфейс распологаем в общем модуле declaration,
 чтобы не плодить модули и не пересоздавать локальные declaration при дальнейшей регруппировке модулей*/
interface DocWebViewerFeature : Feature {
    /**
     * открытие документа в WebView, стартует новая activity
     *
     * @param context
     * @param title заголовок документа
     * @param url адрес ссылки для открытия в WebView
     */
    fun showDocumentLink(context: Context, title: String?, url: String)

    /**
     * открытие документа в WebView, стартует новая activity
     *
     * @param context
     * @param title заголовок документа
     * @param url адрес документа
     * @param uuid идентификатор документа
     */
    fun showDocumentLink(context: Context, title: String?, url: String, uuid: String?)

    /**
     *  создать Intent для просмотра документа в Activity с WebView
     *
     * @param context
     * @param title заголовок документа
     * @param url адрес документа
     * @param uuid идентификатор документа
     * @return Intent для старта activity с установленными параметрами
     */
    fun createDocumentActivityIntent(context: Context, title: String?, url: String, uuid: String?): Intent
}