package ru.tensor.sbis.design.change_theme.settings_item

import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.change_theme.R
import ru.tensor.sbis.design.change_theme.contract.ThemeChangeFragmentProvider
import ru.tensor.sbis.design.change_theme.util.SystemThemes
import ru.tensor.sbis.design.change_theme.util.Theme
import ru.tensor.sbis.settings_screen_common.content.button.Button
import ru.tensor.sbis.settings_screen_common.content.button.asString
import ru.tensor.sbis.settings_screen_common.content.common.ShowUniversalFragment

/**
 * Фабрика создания кнопки смены тем в настройках приложения.
 *
 * @author da.zolotarev
 */
object ChangeThemeButtonFactory {

    /**
     * Фабричный метод
     *
     * @param feature фича поставляющая фрагмент экрана смены тем.
     * @param themes список тем приложения.
     * @param defaultTheme тема, выбранная по-умолчанию.
     */
    fun create(
        feature: ThemeChangeFragmentProvider,
        themes: List<Theme>,
        defaultTheme: Theme,
        systemThemes: SystemThemes? = null
    ) =
        Button(
            titleRes = R.string.change_theme_item_title,
            leftIcon = SbisMobileIcon.Icon.smi_Theme.asString(),
            action = ShowUniversalFragment { _, withNavigation ->
                feature.getThemeSettingsFragment(withNavigation, themes, defaultTheme, systemThemes)
            }
        )
}
