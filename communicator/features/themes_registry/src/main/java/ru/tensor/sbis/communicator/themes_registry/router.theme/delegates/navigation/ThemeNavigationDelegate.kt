package ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.navigation

import ru.tensor.sbis.communicator.themes_registry.router.theme.ThemeRouterInitializer
import ru.tensor.sbis.deeplink.DeeplinkAction

/**
 * Делегат базового функционала навигации реестра диалогов
 *
 * @author vv.chekurda
 */
internal interface ThemeNavigationDelegate :
    ThemeRouterInitializer {

    /**
     * Обработка внешнего [DeeplinkAction]
     *
     * @param args аргументы диплинки
     */
    fun onNewDeeplinkAction(args: DeeplinkAction)

    /**
     * Показать документ в веб-вью
     *
     * @param url   ссылка на документ
     * @param title заголовок документа
     */
    fun showLinkInWebView(url: String, title: String?)

    /**
     * Закрыть контент в details контейнере планшета
     */
    fun closeSubContent()
}