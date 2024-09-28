package ru.tensor.sbis.design.recipient_selection.ui.di.screen

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionUseCase
import ru.tensor.sbis.design.recipient_selection.contract.RecipientSelectionDependency
import ru.tensor.sbis.design.recipient_selection.domain.factory.RecipientHeaderButtonContractFactory
import ru.tensor.sbis.design.recipient_selection.domain.factory.RecipientPreselectionProvider
import ru.tensor.sbis.design.recipient_selection.domain.factory.RecipientSelectionControllerProvider
import ru.tensor.sbis.design.recipient_selection.domain.factory.RecipientItem
import ru.tensor.sbis.design.recipient_selection.domain.factory.RecipientSelectionItemMapper
import ru.tensor.sbis.design.recipient_selection.ui.items.RecipientSelectionCustomization
import ru.tensor.sbis.design_selection.contract.customization.SelectionCustomization
import ru.tensor.sbis.design_selection.contract.filter.SelectionFilterFactory
import ru.tensor.sbis.design_selection.contract.header_button.HeaderButtonContract
import ru.tensor.sbis.design_selection_common.controller.SelectionSourcesImportHelper
import ru.tensor.sbis.design_selection_common.factory.DefaultSelectionFilterFactory

/**
 * Di-модуль компонента выбора получателей.
 *
 * @author vv.chekurda
 */
@Module
internal class RecipientSelectionModule {

    @Provides
    @RecipientSelectionScope
    fun provideRecipientSelectionUseCase(
        config: RecipientSelectionConfig
    ): RecipientSelectionUseCase =
        config.useCase

    @Provides
    @RecipientSelectionScope
    fun provideSelectionSourcesImportHelper(
        useCase: RecipientSelectionUseCase,
        dependency: RecipientSelectionDependency
    ): SelectionSourcesImportHelper =
        SelectionSourcesImportHelper(
            useCase,
            listOfNotNull(dependency.selectionFacesSource)
        )

    @Provides
    @RecipientSelectionScope
    fun provideRecipientSelectionControllerProvider(
        config: RecipientSelectionConfig,
        mapper: RecipientSelectionItemMapper,
        preselectionProvider: RecipientPreselectionProvider,
        sourcesImportHelper: SelectionSourcesImportHelper
    ): RecipientSelectionControllerProvider =
        RecipientSelectionControllerProvider(
            config,
            mapper,
            preselectionProvider,
            sourcesImportHelper
        )

    @Provides
    @RecipientSelectionScope
    fun provideSelectionFilterFactory(
        mapper: RecipientSelectionItemMapper
    ): SelectionFilterFactory<*, *> =
        DefaultSelectionFilterFactory(mapper)

    @Provides
    @RecipientSelectionScope
    fun provideHeaderButtonContract(
        config: RecipientSelectionConfig,
        factory: RecipientHeaderButtonContractFactory
    ): HeaderButtonContract<RecipientItem, *>? =
        factory.createContact(config)

    @Provides
    @RecipientSelectionScope
    fun provideSelectionCustomization(
        dependency: RecipientSelectionDependency,
        config: RecipientSelectionConfig
    ): SelectionCustomization<RecipientItem> =
        RecipientSelectionCustomization(
            config.itemsMode,
            dependency.personClickListener.takeIf { config.canShowPersonCards }
        )
}