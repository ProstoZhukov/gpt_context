package ru.tensor.sbis.dashboard_builder.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.tensor.sbis.android_ext_decl.getParcelableUniversally
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.util.theme.SbisThemedContextFactory
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.dashboard_builder.screen.di.DaggerDashboardScreenComponent
import ru.tensor.sbis.dashboard_builder.screen.ui.DashboardScreenViewBinding
import ru.tensor.sbis.dashboard_builder.screen.ui.DashboardScreenViewImpl
import ru.tensor.sbis.widget_player.api.WidgetPlayerHost
import ru.tensor.sbis.toolbox_decl.dashboard.DashboardRequest
import ru.tensor.sbis.toolbox_decl.dashboard.DashboardScreenOptions
import ru.tensor.sbis.dashboard_builder.R
import ru.tensor.sbis.dashboard_builder.screen.ui.DashboardScreenController

/**
 * @author am.boldinov
 */
class DashboardScreenFragment : BaseFragment(), WidgetPlayerHost {

    companion object {

        private const val REQUEST_KEY = "DASHBOARD_REQUEST"
        private const val OPTIONS_KEY = "DASHBOARD_OPTIONS"

        @JvmStatic
        fun newInstance(
            request: DashboardRequest,
            options: DashboardScreenOptions = DashboardScreenOptions()
        ): Fragment {
            return DashboardScreenFragment().withArgs {
                putParcelable(REQUEST_KEY, request)
                putParcelable(OPTIONS_KEY, options)
            }
        }
    }

    override val childContainerId = R.id.dashboard_screen_overlay_navigation

    private lateinit var controller: DashboardScreenController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controller = DaggerDashboardScreenComponent.factory().create(
            request = requireArguments().getParcelableUniversally<DashboardRequest>(REQUEST_KEY)!!,
            themedContext = SbisThemedContextFactory.createFrom(this),
            viewFactory = {
                DashboardScreenViewImpl(
                    binding = DashboardScreenViewBinding.bind(it),
                    options = requireArguments().getParcelableUniversally<DashboardScreenOptions>(OPTIONS_KEY)!!
                )
            }
        ).injector().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return DashboardScreenViewBinding.inflate(inflater).root
    }

    override fun onBackPressed(): Boolean {
        return controller.dispatchOnBackPressed()
    }
}