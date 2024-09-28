package ru.tensor.sbis.communicator.themes_registry.router.theme.routers

import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.ThemeRouteAction
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.ThemeRoute
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.ThemeRouteType
import ru.tensor.sbis.communicator.themes_registry.router.theme.routers.types.ThemeRouteBuilder

/**
 * Билдер типизированной маршрутизации реестра диалогов
 * @see [ThemeTypedRouting]
 *
 * @author vv.chekurda
 */
internal class ThemeTypedRoutingBuilder {

    /**
     * Список типизированных маршрутов
     */
    private val themeTypedRoutes = mutableListOf<ThemeRoute>()

    /**
     * Дефолтное действие, которое выполнится при 0 совпадений
     */
    private var defaultAction: ThemeRouteAction? = null

    /**
     * Колбэк с типом результирующего маршрута
     */
    private lateinit var routeCallback: PublishSubject<ThemeRouteType>

    /**
     * Колбэк о закрытии экрана с типом маршрута
     */
    private lateinit var closeCallback: PublishSubject<ThemeRouteType>

    /**
     * Регистрация маршрута
     *
     * @param init инициализация маршрута с помощью билдера
     */
    fun registerRoute(init: ThemeRouteBuilder.() -> Unit) {
        themeTypedRoutes.add(ThemeRouteBuilder().apply(init).build())
    }

    /**
     * Добавить колбэк по результату типа [ThemeRouteType] открываемого экрана
     */
    fun addCallback(callback: PublishSubject<ThemeRouteType>) {
        routeCallback = callback
    }

    /**
     * Добавить колбэк по результату типа [ThemeRouteType] закрываемого экрана
     */
    fun addCloseCallback(callback: PublishSubject<ThemeRouteType>) {
        closeCallback = callback
    }

    /**
     * Создать модель типизированных маршрутов
     */
    fun build() =
        ThemeTypedRouting(
            themeTypedRoutes,
            routeCallback,
            closeCallback
        ) { defaultAction ?: Unit }
}