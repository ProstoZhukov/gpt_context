package ru.tensor.sbis.design.universal_selection.ui.di.screen

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.communication_decl.selection.universal.UniversalSelectionConfig
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalItem
import ru.tensor.sbis.design.universal_selection.domain.factory.result.UniversalSelectionResultListener
import ru.tensor.sbis.design_selection.contract.customization.SelectionCustomization
import ru.tensor.sbis.design_selection.contract.filter.SelectionFilterFactory

/**
 * DI-компонент универсального выбора.
 *
 * @author vv.chekurda
 */
@UniversalSelectionScope
@Component(modules = [UniversalSelectionModule::class])
internal interface UniversalSelectionComponent {

    val controllerProvider: UniversalSelectionControllerProvider
    val resultListener: UniversalSelectionResultListener
    val filterFactory: SelectionFilterFactory<*, *>
    val customization: SelectionCustomization<UniversalItem>

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance config: UniversalSelectionConfig): UniversalSelectionComponent
    }
}