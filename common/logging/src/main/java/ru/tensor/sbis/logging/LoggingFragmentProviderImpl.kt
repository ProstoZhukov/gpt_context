package ru.tensor.sbis.logging

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.ContainerBottomSheet
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.MovablePanelVisualMode
import ru.tensor.sbis.logging.log_packages.presentation.LogPackagesFragment
import ru.tensor.sbis.logging.settings.view.LogSettingsFragment
import ru.tensor.sbis.logging.settings.view.LogSettingsSelectionFragment
import ru.tensor.sbis.toolbox_decl.logging.LoggingFragmentProvider

/**
 * Реализация [LoggingFragmentProvider].
 *
 * @author av.krymov
 */
class LoggingFragmentProviderImpl : LoggingFragmentProvider {

    override fun getLoggingHostFragment(withNavigation: Boolean): Fragment =
        LoggingHostFragment.newInstance(withNavigation)

    override fun getLoggingFragment(): Fragment = LogPackagesFragment()

    override fun getLoggingSettingsFragment(): Fragment = LogSettingsFragment()

    override fun getLoggingSettingsDialogFragment(): DialogFragment {
        val container = ContainerBottomSheet().instant(true).setVisualMode(MovablePanelVisualMode())
        return container.setContentCreator(LogSettingsSelectionFragment.Creator())
    }
}