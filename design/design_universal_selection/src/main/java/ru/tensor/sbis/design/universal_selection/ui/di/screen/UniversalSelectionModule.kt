package ru.tensor.sbis.design.universal_selection.ui.di.screen

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.communication_decl.selection.universal.UniversalSelectionConfig
import ru.tensor.sbis.communication_decl.selection.universal.data.UniversalItemId
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalPreselectionProvider
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalItem
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalSelectionItemMapper
import ru.tensor.sbis.design.universal_selection.domain.factory.customization.UniversalSelectionCustomization
import ru.tensor.sbis.design_selection.contract.customization.SelectionCustomization
import ru.tensor.sbis.design_selection.contract.filter.SelectionFilterFactory
import ru.tensor.sbis.design_selection_common.controller.SelectionControllerProviderImpl
import ru.tensor.sbis.design_selection_common.factory.DefaultSelectionFilterFactory

internal typealias UniversalSelectionControllerProvider =
    SelectionControllerProviderImpl<UniversalItem, UniversalItemId>

/**
 * Di-модуль компонента универсального выбора.
 *
 * @author vv.chekurda
 */
@Module
internal class UniversalSelectionModule {

    @Provides
    @UniversalSelectionScope
    fun provideUniversalSelectionControllerProvider(
        config: UniversalSelectionConfig,
        mapper: UniversalSelectionItemMapper,
        preselectionProvider: UniversalPreselectionProvider
    ): UniversalSelectionControllerProvider =
        UniversalSelectionControllerProvider(config, mapper, preselectionProvider)

    @Provides
    @UniversalSelectionScope
    fun provideSelectionFilterFactory(mapper: UniversalSelectionItemMapper): SelectionFilterFactory<*, *> =
        DefaultSelectionFilterFactory(mapper)

    @Provides
    @UniversalSelectionScope
    fun provideSelectionCustomization(): SelectionCustomization<UniversalItem> =
        UniversalSelectionCustomization()
}