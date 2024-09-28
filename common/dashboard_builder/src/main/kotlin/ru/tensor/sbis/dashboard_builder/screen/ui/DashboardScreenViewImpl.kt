package ru.tensor.sbis.dashboard_builder.screen.ui

import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import ru.tensor.sbis.dashboard_builder.screen.ui.DashboardScreenView.Model
import ru.tensor.sbis.dashboard_builder.screen.ui.DashboardScreenView.Event
import ru.tensor.sbis.toolbox_decl.dashboard.DashboardScreenOptions

/**
 * @author am.boldinov
 */
internal class DashboardScreenViewImpl(
    private val binding: DashboardScreenViewBinding,
    private val options: DashboardScreenOptions
) : BaseMviView<Model, Event>(), DashboardScreenView {

    init {
        with(binding) {
            widgetPlayer.floatingPanelPadding = options.floatingPanelPadding
            widgetPlayer.navigationPanelPadding = options.navigationPanelPadding
        }
    }

    override val renderer: ViewRenderer<Model> = diff {
        diff(get = Model::topNavigationContent, set = {
            binding.topNavigationView.content = it
        })
        diff(get = Model::source, set = {
            binding.widgetPlayer.setWidgetSource(it)
        })
    }
}