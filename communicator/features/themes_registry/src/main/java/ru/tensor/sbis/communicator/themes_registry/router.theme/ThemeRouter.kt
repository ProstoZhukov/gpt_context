package ru.tensor.sbis.communicator.themes_registry.router.theme

import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction
import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.conversation.ThemeConversationRouter
import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.external.ThemeExternalScreenRouter
import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.navigation.ThemeNavigationDelegate
import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.new_conversation.ThemeNewConversationRouter
import ru.tensor.sbis.communicator.themes_registry.router.theme.delegates.participants.ThemeParticipantsRouter
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.ThemeConversationParams
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.ThemeRouteType
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.ThemeFragment

/**
 * Роутер реестра диалогов
 * @see [ThemeRouterInitializer]     инициализатор роутера
 * @see [ThemeConversationRouter]    роутер открытия переписки
 * @see [ThemeNewConversationRouter] роутер создания новой переписки
 * @see [ThemeParticipantsRouter]    роутер участников диалога/чата
 * @see [ThemeExternalScreenRouter]  роутер экранов внешних зависимостей
 * @see [ThemeNavigationDelegate]    делегат базовой навигации модуля
 *
 * @author vv.chekurda
 */
internal interface ThemeRouter :
    ThemeRouterInitializer,
    ThemeConversationRouter,
    ThemeNewConversationRouter,
    ThemeParticipantsRouter,
    ThemeExternalScreenRouter,
    ThemeNavigationDelegate {

    /**
     * Колбэк о типе [ThemeRouteType] открываемого контента
     */
    val routeCallback: PublishSubject<ThemeRouteType>

    /**
     * Колбэк о закрытии с типом [ThemeRouteType] экрана
     */
    val closeCallback: PublishSubject<ThemeRouteType>

    /**
     * Открыть экран
     *
     * @param routeParams параметры
     */
    fun openContentScreen(routeParams: ThemeConversationParams)

    fun openConversationPreview(
        routeParams: ThemeConversationParams,
        list: List<ConversationPreviewMenuAction.ThemeConversationPreviewMenuAction>
    )

    /**
     * Открыть ссылку в вебвью.
     *
     * @param link ссылка которую необходимо отобразить.
     */
    fun openLinkInWebView(link: String)
}

/**
 * Инициализатор роутера реестра диалогов
 */
internal interface ThemeRouterInitializer {

    /**
     * Проинициализировать роутер
     *
     * @param fragment фрагмент реестра
     */
    fun initRouter(fragment: ThemeFragment)
}