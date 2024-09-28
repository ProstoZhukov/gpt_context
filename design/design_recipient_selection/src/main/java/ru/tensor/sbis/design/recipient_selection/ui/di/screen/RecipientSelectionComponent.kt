package ru.tensor.sbis.design.recipient_selection.ui.di.screen

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.design.recipient_selection.ui.di.singleton.RecipientSelectionSingletonComponent
import ru.tensor.sbis.design.recipient_selection.domain.factory.RecipientSelectionControllerProvider
import ru.tensor.sbis.design.recipient_selection.domain.factory.RecipientItem
import ru.tensor.sbis.design.recipient_selection.domain.factory.result.RecipientSelectionResultListener
import ru.tensor.sbis.design_selection.contract.customization.SelectionCustomization
import ru.tensor.sbis.design_selection.contract.filter.SelectionFilterFactory
import ru.tensor.sbis.design_selection.contract.header_button.HeaderButtonContract

/**
 * DI-компонент выбора получателей.
 *
 * @author vv.chekurda
 */
@RecipientSelectionScope
@Component(
    modules = [RecipientSelectionModule::class],
    dependencies = [RecipientSelectionSingletonComponent::class]
)
internal interface RecipientSelectionComponent {

    val controllerProvider: RecipientSelectionControllerProvider
    val filterFactory: SelectionFilterFactory<*, *>
    val resultListener: RecipientSelectionResultListener
    val headerButtonContract: HeaderButtonContract<RecipientItem, *>?
    val selectionCustomization: SelectionCustomization<RecipientItem>

    @Component.Factory
    interface Factory {
        fun create(
            singletonComponent: RecipientSelectionSingletonComponent,
            @BindsInstance config: RecipientSelectionConfig
        ): RecipientSelectionComponent
    }
}