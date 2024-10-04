package ru.tensor.sbis.design.change_theme

import ru.tensor.sbis.design.change_theme.contract.ThemeChangeFragmentProvider
import ru.tensor.sbis.design.change_theme.contract.ThemeChangeFragmentProviderImpl
import ru.tensor.sbis.design.change_theme.contract.ThemesProvider
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин экрана смены тем.
 *
 * @author da.zolotarev
 */
object ChangeThemePlugin : BasePlugin<Unit>() {

    private val themeChangeFragmentProvider by lazy {
        ThemeChangeFragmentProviderImpl()
    }

    /**
     * Список тем приложения.
     */
    var themesProvider: FeatureProvider<ThemesProvider>? = null

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(ThemeChangeFragmentProvider::class.java) { themeChangeFragmentProvider }
    )

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .require(ThemesProvider::class.java) { themesProvider = it }
            .build()
    }

    override val customizationOptions = Unit
}