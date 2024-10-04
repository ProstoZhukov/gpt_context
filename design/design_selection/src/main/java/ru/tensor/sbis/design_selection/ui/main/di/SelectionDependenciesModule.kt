package ru.tensor.sbis.design_selection.ui.main.di

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.communication_decl.selection.SelectionItemId
import ru.tensor.sbis.design_selection.contract.SelectionDependenciesFactory
import ru.tensor.sbis.design_selection.contract.customization.SelectionCustomization
import ru.tensor.sbis.design_selection.contract.customization.SelectionStrings
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.filter.SelectionFilterFactory
import ru.tensor.sbis.design_selection.contract.header_button.HeaderButtonContract
import ru.tensor.sbis.design_selection.contract.listeners.SelectionResultListener
import ru.tensor.sbis.design_selection.domain.SelectionControllerHolder
import ru.tensor.sbis.design_selection.ui.content.di.SelectionStubFactory

/**
 * DI компонент зависимостей компонента выбора.
 *
 * @author vv.chekurda
 */
@Module
@Suppress("UNCHECKED_CAST")
internal class SelectionDependenciesModule {

    @Provides
    fun provideApplicationContext(fragment: Fragment): Context =
        fragment.requireContext().applicationContext

    @Provides
    @SelectionScope
    fun providerSelectionDependenciesFactory(
        dependenciesProvider: SelectionDependenciesFactory.Provider<*, *>
    ): SelectionDependenciesFactory.Provider<SelectionItem, SelectionConfig> =
        dependenciesProvider as SelectionDependenciesFactory.Provider<SelectionItem, SelectionConfig>

    @Provides
    @SelectionScope
    fun provideSelectionDependenciesFactory(
        appContext: Context,
        config: SelectionConfig,
        dependenciesProvider: SelectionDependenciesFactory.Provider<SelectionItem, SelectionConfig>
    ): SelectionDependenciesFactory<SelectionItem> =
        dependenciesProvider.getFactory(appContext, config)

    @Provides
    @SelectionScope
    fun provideSelectionControllerHolderFactory(
        appContext: Context,
        dependenciesFactory: SelectionDependenciesFactory<SelectionItem>
    ): SelectionControllerHolder.Factory =
        SelectionControllerHolder.Factory(
            lazy { dependenciesFactory.getSelectionControllerProvider(appContext) as SelectionControllerProvider }
        )

    @Provides
    @SelectionScope
    fun provideSelectionControllerHolder(
        fragment: Fragment,
        factory: SelectionControllerHolder.Factory
    ): SelectionControllerHolder =
        ViewModelProvider(fragment, factory)[SelectionControllerHolder::class.java]

    @Provides
    @SelectionScope
    fun provideSelectionResultListener(
        appContext: Context,
        dependenciesFactory: SelectionDependenciesFactory<SelectionItem>
    ): SelectionResultListener<SelectionItem, FragmentActivity> =
        dependenciesFactory.getSelectionResultListener(appContext)
            as SelectionResultListener<SelectionItem, FragmentActivity>

    @Provides
    @SelectionScope
    fun provideFilterFactory(
        appContext: Context,
        dependenciesFactory: SelectionDependenciesFactory<SelectionItem>
    ): SelectionFilterFactory<Any, SelectionItemId> =
        dependenciesFactory.getFilterFactory(appContext) as SelectionFilterFactory<Any, SelectionItemId>

    @Provides
    @SelectionScope
    fun provideStubFactory(
        appContext: Context,
        dependenciesFactory: SelectionDependenciesFactory<SelectionItem>
    ): SelectionStubFactory =
        dependenciesFactory.getStubFactory(appContext)

    @Provides
    @SelectionScope
    fun provideSelectorStrings(
        appContext: Context,
        dependenciesFactory: SelectionDependenciesFactory<SelectionItem>,
        config: SelectionConfig
    ): SelectionStrings =
        dependenciesFactory.getSelectorStrings(appContext, config)

    @Provides
    @SelectionScope
    fun provideHeaderButtonContract(
        appContext: Context,
        dependenciesFactory: SelectionDependenciesFactory<SelectionItem>
    ): HeaderButtonContract<SelectionItem, FragmentActivity>? =
        dependenciesFactory.getHeaderButtonContract(appContext)
            as HeaderButtonContract<SelectionItem, FragmentActivity>?

    @Provides
    @SelectionScope
    fun provideSelectionCustomization(
        appContext: Context,
        dependenciesFactory: SelectionDependenciesFactory<SelectionItem>
    ): SelectionCustomization<SelectionItem> =
        dependenciesFactory.getSelectionCustomization(appContext)
}