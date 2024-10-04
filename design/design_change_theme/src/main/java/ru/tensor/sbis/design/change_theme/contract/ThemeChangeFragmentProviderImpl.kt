package ru.tensor.sbis.design.change_theme.contract

import androidx.fragment.app.Fragment
import ru.tensor.sbis.design.change_theme.util.SystemThemes
import ru.tensor.sbis.design.change_theme.util.Theme
import ru.tensor.sbis.design.change_theme.view.ChangeThemeFragment

/**
 * Реализация фичи [ThemeChangeFragmentProvider].
 *
 * @author da.zolotarev
 */
internal class ThemeChangeFragmentProviderImpl : ThemeChangeFragmentProvider {
    override fun getThemeSettingsFragment(
        withNavigation: Boolean,
        themes: List<Theme>,
        defaultTheme: Theme,
        systemThemes: SystemThemes?
    ): Fragment {
        return ChangeThemeFragment.create(withNavigation, themes, defaultTheme, systemThemes)
    }
}