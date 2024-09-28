package ru.tensor.sbis.dashboard_builder.screen.ui

import android.view.View
import com.arkivanov.mvikotlin.core.view.MviView
import ru.tensor.sbis.dashboard_builder.screen.ui.DashboardScreenView.Model
import ru.tensor.sbis.dashboard_builder.screen.ui.DashboardScreenView.Event
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.widget_player.api.WidgetSource

/**
 * @author am.boldinov
 */
internal interface DashboardScreenView : MviView<Model, Event> {

    sealed interface Event

    data class Model(
        val topNavigationContent: SbisTopNavigationContent,
        val source: WidgetSource
    )

    fun interface Factory : (View) -> DashboardScreenView
}