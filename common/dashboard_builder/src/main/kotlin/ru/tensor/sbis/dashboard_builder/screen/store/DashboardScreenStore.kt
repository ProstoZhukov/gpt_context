package ru.tensor.sbis.dashboard_builder.screen.store

import com.arkivanov.mvikotlin.core.store.Store
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.widget_player.api.WidgetSource

/**
 * @author am.boldinov
 */
internal interface DashboardScreenStore :
    Store<DashboardScreenStore.Intent, DashboardScreenStore.State, DashboardScreenStore.Label> {

    sealed interface Intent

    sealed interface Label

    data class State(
        val source: WidgetSource = WidgetSource.Body(body = null),
        val topNavigationContent: SbisTopNavigationContent = SbisTopNavigationContent.EmptyContent
    )
}