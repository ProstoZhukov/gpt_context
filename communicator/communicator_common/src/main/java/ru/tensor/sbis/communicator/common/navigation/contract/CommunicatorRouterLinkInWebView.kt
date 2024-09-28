package ru.tensor.sbis.communicator.common.navigation.contract

/**
 * Вспомогательный интерфейс для показа ссылки в веб-вью
 *
 * @author da.zhukov
 */
interface CommunicatorRouterLinkInWebView {

    /**
     * Показать ссылку в веб-вью
     *
     * @param url   ссылка документа
     * @param title заголовок документа
     */
    fun showLinkInWebView(url: String, title: String? = null)
}