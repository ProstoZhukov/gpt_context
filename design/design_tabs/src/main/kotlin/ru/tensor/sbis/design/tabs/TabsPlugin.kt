package ru.tensor.sbis.design.tabs

import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.toolbox_decl.toolbar.ToolbarTabsController

/**
 * Плагин компонента вкладок.
 *
 * @author us.bessonov
 */
object TabsPlugin : BasePlugin<Unit>() {

    private var tabsControllerFeature: FeatureProvider<ToolbarTabsController>? = null

    internal val tabsController: ToolbarTabsController?
        get() = tabsControllerFeature?.get()

    override val api: Set<FeatureWrapper<out Feature>> = emptySet()

    override val dependency: Dependency = Dependency.Builder()
        .optional(ToolbarTabsController::class.java) { tabsControllerFeature = it }
        .build()

    override val customizationOptions = Unit
}