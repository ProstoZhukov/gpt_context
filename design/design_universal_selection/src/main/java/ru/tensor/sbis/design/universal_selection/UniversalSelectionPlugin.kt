package ru.tensor.sbis.design.universal_selection

import ru.tensor.sbis.communication_decl.selection.universal.UniversalSelectionProvider
import ru.tensor.sbis.communication_decl.selection.universal.manager.UniversalSelectionResultDelegate
import ru.tensor.sbis.design.universal_selection.contract.UniversalSelectionFeatureFacade
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин модуля универсального выбора.
 *
 * @author vv.chekurda
 */
object UniversalSelectionPlugin : BasePlugin<Unit>() {

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(UniversalSelectionProvider::class.java) { UniversalSelectionFeatureFacade },
        FeatureWrapper(UniversalSelectionResultDelegate.Provider::class.java) { UniversalSelectionFeatureFacade }
    )

    override val dependency: Dependency = Dependency.EMPTY

    override val customizationOptions: Unit = Unit
}