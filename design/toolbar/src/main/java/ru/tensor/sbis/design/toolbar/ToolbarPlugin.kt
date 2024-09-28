package ru.tensor.sbis.design.toolbar

import ru.tensor.sbis.design.toolbar.util.ToolbarTabsControllerImpl
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.toolbox_decl.toolbar.ToolbarTabsController

/**
 * Плагин модуля toolbar.
 *
 * @author us.bessonov
 */
object ToolbarPlugin : BasePlugin<Unit>() {

    internal val tabsVisibilityController by lazy {
        ToolbarTabsControllerImpl()
    }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(ToolbarTabsController::class.java) { tabsVisibilityController }
    )

    override val dependency = Dependency.EMPTY

    override val customizationOptions = Unit
}