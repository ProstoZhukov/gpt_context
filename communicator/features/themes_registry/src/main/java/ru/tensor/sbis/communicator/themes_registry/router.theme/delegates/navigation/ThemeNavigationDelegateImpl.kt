package ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.navigation

import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.BaseThemeRouterDelegate
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.ThemeFragment
import ru.tensor.sbis.deeplink.DeeplinkAction

/**
 * Реализация делегата базового функционала навигации реестра диалогов
 * @see [ThemeNavigationDelegate]
 *
 * @author vv.chekurda
 */
internal class ThemeNavigationDelegateImpl :
    BaseThemeRouterDelegate(),
    ThemeNavigationDelegate {

    private var postponedDeeplinkAction: DeeplinkAction? = null

    override fun initRouter(fragment: ThemeFragment) {
        super.initRouter(fragment)
        postponedDeeplinkAction?.let {
            onNewDeeplinkAction(it)
            postponedDeeplinkAction = null
        }
    }

    override fun onNewDeeplinkAction(args: DeeplinkAction) {
        communicatorThemesRouter?.handleDeeplinkAction(args)
            ?: run { postponedDeeplinkAction = args }
    }

    override fun showLinkInWebView(url: String, title: String?) {
        communicatorThemesRouter?.showLinkInWebView(url, title)
    }

    override fun closeSubContent() {
        communicatorThemesRouter?.removeSubContent()
    }
}