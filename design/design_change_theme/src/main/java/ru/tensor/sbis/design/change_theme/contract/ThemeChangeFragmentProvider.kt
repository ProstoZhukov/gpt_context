package ru.tensor.sbis.design.change_theme.contract

import androidx.fragment.app.Fragment
import ru.tensor.sbis.design.change_theme.util.SystemThemes
import ru.tensor.sbis.design.change_theme.util.Theme
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фича для получения экрана смены темы в настройках приложения.
 *
 * @author da.zolotarev
 */
interface ThemeChangeFragmentProvider : Feature {
    /**
     *  @param withNavigation включена ли навигация назад (стрелка в toolbar).
     *  @param themes         список тем отображаемых на экране.
     *  @param defaultTheme   тема, выбранная по-умолчанию.
     *  @param systemThemes   объект с темами для дневного\ночного режимов для применения в режиме системной темизации.
     *                        Если null - на экране не будет отображаться переключатель на системную темизацию.
     */
    fun getThemeSettingsFragment(
        withNavigation: Boolean,
        themes: List<Theme>,
        defaultTheme: Theme,
        systemThemes: SystemThemes? = null
    ): Fragment
}