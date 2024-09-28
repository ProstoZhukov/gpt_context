package ru.tensor.sbis.application_tools

import androidx.fragment.app.Fragment
import ru.tensor.sbis.application_tools.debuginfo.DebugInfoSettingsFragment
import ru.tensor.sbis.application_tools.fontcheck.SettingsFontCheckFragment
import ru.tensor.sbis.settings_screen_decl.AppToolsSettingsFragmentProvider

/**
 * Реализация [AppToolsSettingsFragmentProvider]
 *
 * @author us.bessonov
 */
class AppToolsSettingsFragmentProviderImpl : AppToolsSettingsFragmentProvider {

    override fun getDebugInfoSettingsFragment(title: String, withNavigation: Boolean): Fragment =
        DebugInfoSettingsFragment.newInstance(title, withNavigation)

    override fun getSettingsFontCheckFragment(withNavigation: Boolean): Fragment =
        SettingsFontCheckFragment.newInstance(withNavigation)
}