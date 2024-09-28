package ru.tensor.sbis.communicator.themes_registry.router.theme.routers

import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.ThemeRouteAction
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.ThemeRoute
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.ThemeRouteType

/**
 * Типизированная маршрутизация реестра диалогов для упрощения навигации
 * при клике элемент списка реестра диалогов
 *
 * @param themeTypedRoutes список типизируемых маршрутов
 * @param routeCallback    колбэк с типом открываемого экрана
 * @param closeCallback    колбэк с типом закрытого экрана
 * @param defaultRoute     стандартный маршрут при отсутствии совпадений
 *
 * @author vv.chekurda
 */
internal class ThemeTypedRouting(
    private val themeTypedRoutes: List<ThemeRoute>,
    private val routeCallback: PublishSubject<ThemeRouteType>,
    private val closeCallback: PublishSubject<ThemeRouteType>,
    private val defaultRoute: ThemeRouteAction
) {
    companion object {
        /**
         * Создание типизированной маршрутизации
         *
         * @param callback колбэк для проброса типов открываемого экрана
         * @param register регистрациия маршрутов через билдер
         */
        operator fun invoke(
            callback: PublishSubject<ThemeRouteType>,
            closeCallback: PublishSubject<ThemeRouteType>,
            register: ThemeTypedRoutingBuilder.() -> Unit
        ) = ThemeTypedRoutingBuilder().run {
            register()
            addCallback(callback)
            addCloseCallback(closeCallback)
            build()
        }
    }

    /**
     * Поиск маршрута по модели диалога
     *
     * @param conversation модель диалога
     * @return [ThemeRoute] подходящий маршрут
     */
    private fun findRoute(conversation: ConversationModel): ThemeRoute? =
        themeTypedRoutes.firstOrNull { it.isTheSameType(conversation) }

    /**
     * Открыть экран по модели параметров диалога
     *
     * @param routeParams параметры диалога
     */
    fun openContentScreen(routeParams: ThemeConversationParams) {
        val route = findRoute(routeParams.model)?.also {
            routeCallback.onNext(it.routeType)
        }
        val onCloseAction = route?.let { { closeCallback.onNext(route.routeType) } }
        route?.invoke(routeParams, onCloseAction)
            ?: defaultRoute.invoke(routeParams.model, null)
    }
}