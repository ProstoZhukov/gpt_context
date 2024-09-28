package ru.tensor.sbis.logging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.util.addNavigationArg
import ru.tensor.sbis.common.util.doIfNavigationDisabled
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.util.ButtonsFactory.createDefaultButton
import ru.tensor.sbis.design.utils.insets.addTopPaddingByInsets
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.ContainerBottomSheet
import ru.tensor.sbis.logging.databinding.LoggingHostFragmentBinding
import ru.tensor.sbis.logging.settings.view.LogSettingsSelectionFragment

/**
 * Хост фрагмент для экрана отправки логов.
 */
internal class LoggingHostFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance(withNavigation: Boolean = true): Fragment =
            addNavigationArg(LoggingHostFragment(), withNavigation)
    }

    private val loggingFeature = LoggingFeatureImpl()

    private var binding: LoggingHostFragmentBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LoggingHostFragmentBinding.inflate(inflater)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (getCurrentFragment() == null) {
            childFragmentManager.beginTransaction()
                .add(
                    R.id.logging_body,
                    loggingFeature.getLoggingFragmentProvider().getLoggingFragment()
                )
                .commit()
        }
        binding?.loggingToolbar?.apply {
            content = SbisTopNavigationContent.SmallTitle(
                title = PlatformSbisString.Res(R.string.logging_host_toolbar_title)
            )
            backBtn?.setOnClickListener {
                activity?.onBackPressed()
            }

            rightItems = listOf(
                createDefaultButton(PlatformSbisString.Icon(SbisMobileIcon.Icon.smi_navBarMore)).apply {
                    setOnClickListener { showSettingsScreen() }
                }
            )
            doIfNavigationDisabled(this@LoggingHostFragment) {
                showBackButton = false
            }
            addTopPaddingByInsets(this)
        }
    }

    override fun onBackPressed(): Boolean {
        if (childFragmentManager.backStackEntryCount > 0) {
            childFragmentManager.popBackStack()
            return true
        }

        return false
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun showSettingsScreen() {
        ContainerBottomSheet()
            .instant(true)
            .setContentCreator(LogSettingsSelectionFragment.Creator())
            .show(
                childFragmentManager,
                LogSettingsSelectionFragment::class.java.canonicalName
            )

    }

    private fun getCurrentFragment(): Fragment? {
        return childFragmentManager.findFragmentById(R.id.logging_body)
    }
}