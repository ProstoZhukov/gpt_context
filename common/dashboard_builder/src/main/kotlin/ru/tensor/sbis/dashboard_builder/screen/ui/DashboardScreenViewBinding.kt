package ru.tensor.sbis.dashboard_builder.screen.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.widget.LinearLayout
import ru.tensor.sbis.dashboard_builder.R
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.widget_player.WidgetPlayer
import ru.tensor.sbis.widget_player.api.ScrollingMode

/**
 * @author am.boldinov
 */
internal class DashboardScreenViewBinding private constructor(val root: ViewGroup) {

    companion object {

        fun inflate(inflater: LayoutInflater): DashboardScreenViewBinding {
            return DashboardScreenViewBinding(
                root = FrameLayout(inflater.context).apply {
                    id = R.id.dashboard_screen_root_layout
                    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    setBackgroundColor(StyleColor.UNACCENTED.getAdaptiveBackgroundColor(context))
                }
            )
        }

        fun bind(root: View): DashboardScreenViewBinding {
            return requireNotNull(root.getTag(R.id.dashboard_screen_view_binding) as? DashboardScreenViewBinding) {
                "DashboardScreenViewBinding was not found for this root view $root"
            }
        }
    }

    private val context get() = root.context

    val topNavigationView = SbisTopNavigationView(context).apply {
        id = R.id.dashboard_screen_top_navigation
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        content = SbisTopNavigationContent.EmptyContent
    }

    val widgetPlayer = WidgetPlayer(context).apply {
        id = R.id.dashboard_screen_widget_player
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        scrollingMode = ScrollingMode.VERTICAL
        navigationPanelPadding = true
    }

    val overlayNavigationContainer = FrameLayout(context).apply {
        id = R.id.dashboard_screen_overlay_navigation
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    init {
        root.setTag(R.id.dashboard_screen_view_binding, this)
        inflateLayout()
    }

    private fun inflateLayout() {
        root.addView(
            LinearLayout(context).apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                orientation = LinearLayout.VERTICAL
                addView(topNavigationView)
                addView(widgetPlayer)
            }
        )
        root.addView(overlayNavigationContainer)
    }
}