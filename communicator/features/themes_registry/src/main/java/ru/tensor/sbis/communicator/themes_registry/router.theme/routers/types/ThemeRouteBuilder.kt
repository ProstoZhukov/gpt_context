package ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types

/**
 * Билдер навигационных маршрутов реестра диалогов
 * @see [ThemeRoute]
 *
 * @author vv.chekurda
 */
internal class ThemeRouteBuilder {

    /**
     * Тип маршрута
     */
    private var routeType: ThemeRouteType = ThemeRouteType.UNKNOWN

    /**
     * Навигационное действие
     */
    private var routeAction: ThemeRouteAction? = null

    /**
     * Навигационное действие для открытия переписки (расширенные аргументы)
     */
    private var conversationRouteAction: ThemeConversationRouteAction? = null

    /**
     * Устновить тип маршрута
     */
    fun type(type: ThemeRouteType) {
        routeType = type
    }

    /**
     * Установить действие маршрута
     */
    fun action(route: ThemeRouteAction) {
        routeAction = route
    }

    /**
     * Установить действие маршрута для открытия переписки
     */
    fun conversationAction(route: ThemeConversationRouteAction) {
        conversationRouteAction = route
    }

    /**
     * Создать модель навигационного маршрута
     */
    fun build() =
        ThemeRoute(
            routeType,
            routeAction,
            conversationRouteAction
        )
}