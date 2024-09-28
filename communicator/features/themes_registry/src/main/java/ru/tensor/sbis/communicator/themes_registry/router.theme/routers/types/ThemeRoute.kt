package ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types

import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.ThemeConversationParams

/**
 * Навигационный маршрут реестра диалогов
 *
 * @property routeType          тип маршрута
 * @property action             действие машрута
 * @property conversationAction действие машрута переписки
 *
 * @author vv.chekurda
 */
internal class ThemeRoute(
    val routeType: ThemeRouteType,
    private val action: ThemeRouteAction?,
    private val conversationAction: ThemeConversationRouteAction?
) {

    /**
     * Проверить на принадлежность модели переписки к текущему типу маршрута.
     */
    fun isTheSameType(conversationModel: ConversationModel): Boolean =
        routeType.isTheSameType(conversationModel)

    operator fun invoke(
        routeParams: ThemeConversationParams,
        closeAction: (() -> Unit)? = null
    ): Unit? =
        conversationAction?.invoke(
            routeParams.model,
            routeParams.isChatTab,
            routeParams.isSearchEmpty,
            routeParams.isArchivedDialog,
            closeAction
        ) ?: action?.invoke(routeParams.model, closeAction)
}

/**
 * Действие маршрута реестра диалогов
 */
internal typealias ThemeRouteAction = ConversationModel.(closeAction: (() -> Unit)?) -> Unit

/**
 * Действие маршрута переписки реестра диалогов (расширенные аргументы [ThemeRouteAction])
 */
internal typealias ThemeConversationRouteAction =
    ConversationModel.(isChatTab: Boolean, isSearchEmpty: Boolean, isArchived: Boolean, closeAction: (() -> Unit)?) -> Unit