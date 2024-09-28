package ru.tensor.sbis.local_feature_toggle_main_screen_addon

import ru.tensor.sbis.main_screen_decl.MainScreenAddon
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин аддона [LocalFeatureToggleMainScreenAddon]
 *
 * @author mb.kruglova
 */
object LocalFeatureToggleMainScreenAddonPlugin : BasePlugin<Unit>() {

    override val dependency: Dependency = Dependency.Builder().build()

    override val api: Set<FeatureWrapper<out Feature>> = emptySet()

    override val customizationOptions = Unit
}

/**
 * Создать аддон раздела фичетогл на главном экране
 */
fun LocalFeatureToggleMainScreenAddonPlugin.createAddon(): MainScreenAddon = LocalFeatureToggleMainScreenAddon()