package ru.tensor.sbis.design.change_theme.contract

import ru.tensor.sbis.design.change_theme.util.SystemThemes
import ru.tensor.sbis.design.change_theme.util.Theme

/**
 * Реализация фичи для получения тем приложения.
 *
 * @author da.zolotarev
 */
class ThemesProviderImpl(
    private val themes: List<Theme>,
    private val systemThemes: SystemThemes? = null
) : ThemesProvider {
    override fun getThemes() = themes
    override fun getSystemThemes() = systemThemes
}