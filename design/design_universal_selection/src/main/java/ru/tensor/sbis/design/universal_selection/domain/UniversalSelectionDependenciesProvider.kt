package ru.tensor.sbis.design.universal_selection.domain

import android.content.Context
import ru.tensor.sbis.communication_decl.selection.universal.UniversalSelectionConfig
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalItem
import ru.tensor.sbis.design.universal_selection.ui.di.screen.DaggerUniversalSelectionComponent
import ru.tensor.sbis.design_selection.contract.SelectionDependenciesFactory

/**
 * Поставщик фабрики зависимостей компонента универсального выбора.
 *
 * @author vv.chekurda
 */
internal class UniversalSelectionDependenciesProvider :
    SelectionDependenciesFactory.Provider<UniversalItem, UniversalSelectionConfig> {

    override fun getFactory(
        appContext: Context,
        config: UniversalSelectionConfig,
    ): SelectionDependenciesFactory<UniversalItem> {
        val component = DaggerUniversalSelectionComponent.factory().create(config)
        return UniversalSelectionDependenciesFactory(component)
    }
}