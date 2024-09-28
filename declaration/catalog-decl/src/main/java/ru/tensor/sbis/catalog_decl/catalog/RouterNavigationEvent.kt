package ru.tensor.sbis.catalog_decl.catalog

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 *  Передача событий навигации.
 *
 *  @author sp.lomakin
 */
interface RouterNavigationEvent : Feature {

    var listener: Listener?

    fun sendNavigationEvent(navigationEvent: NavigationEvent)

    interface Listener {
        fun onNavigationEvent(navigationEvent: NavigationEvent)
    }
}

interface RouterNavigationEventProvider {

    fun getRouterNavigationEvents(): RouterNavigationEvent

}

/**
 *  Базовый интерфейс события навигации.
 *
 *  @author sp.lomakin
 */
interface NavigationEvent
