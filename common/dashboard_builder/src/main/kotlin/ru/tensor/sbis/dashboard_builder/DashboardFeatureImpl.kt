package ru.tensor.sbis.dashboard_builder

import androidx.fragment.app.Fragment
import ru.tensor.sbis.dashboard_builder.screen.DashboardScreenFragment
import ru.tensor.sbis.toolbox_decl.dashboard.DashboardRequest
import ru.tensor.sbis.toolbox_decl.dashboard.DashboardScreenOptions
import ru.tensor.sbis.toolbox_decl.dashboard.DashboardScreenProvider

/**
 * @author am.boldinov
 */
internal class DashboardFeatureImpl : DashboardScreenProvider {

    override fun getDashboardScreenFragment(request: DashboardRequest, options: DashboardScreenOptions): Fragment {
        return DashboardScreenFragment.newInstance(
            request = request,
            options = options
        )
    }
}