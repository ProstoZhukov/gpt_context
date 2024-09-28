package ru.tensor.sbis.design.selection.ui.di.common

import android.os.Bundle
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.selection.ui.contract.SelectorStrings
import ru.tensor.sbis.design.selection.ui.contract.list.PrefetchCheckFunction
import ru.tensor.sbis.design.selection.ui.contract.list.ResultMapper
import ru.tensor.sbis.design.selection.ui.di.SelectionListScreenScope
import ru.tensor.sbis.design.selection.ui.di.resolveStubContentProvider
import ru.tensor.sbis.design.selection.ui.factories.FilterFactory
import ru.tensor.sbis.design.selection.ui.list.SelectionListScreenEntityFactory
import ru.tensor.sbis.design.selection.ui.list.filter.SelectorFilterCreator
import ru.tensor.sbis.design.selection.ui.list.filter.SelectorListFilterMetaFactory
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.customStubContentProvider
import ru.tensor.sbis.design.selection.ui.utils.parentItemIdArg
import ru.tensor.sbis.design.selection.ui.utils.prefetchCheckFunction
import ru.tensor.sbis.design.selection.ui.utils.stub.StubContentProviderAdapter
import ru.tensor.sbis.list.base.data.ResultHelper
import ru.tensor.sbis.list.base.domain.entity.paging.PagingEntity
import javax.inject.Named

@Module(includes = [MapperModule::class])
internal class EntityFactoryModule {

    @Provides
    @SelectionListScreenScope
    fun provideFilterCreator(
        filterFactory: FilterFactory<SelectorItemModel, Any, Any>,
        metaFactory: SelectorListFilterMetaFactory<Any>,
        @Named(LIST_ARGUMENTS)
        arguments: Bundle
    ): SelectorFilterCreator<Any, Any> =
        SelectorFilterCreator(
            filterFactory,
            metaFactory,
            arguments.parentItemIdArg
        )

    @Provides
    @SelectionListScreenScope
    fun providePagingEntity(
        mapper: ResultMapper<Any>,
        helper: ResultHelper<Any, Any>,
        stubContentProvider: StubContentProviderAdapter<Any>
    ): PagingEntity<Any, Any, Any> =
        PagingEntity(
            mapper,
            helper,
            stubContentProvider
        )

    @Provides
    @SelectionListScreenScope
    fun provideEntityFactory(
        mapper: ResultMapper<Any>,
        filterCreator: SelectorFilterCreator<Any, Any>,
        prefetchCheckFunction: PrefetchCheckFunction<SelectorItemModel>,
        stubContentProvider: StubContentProviderAdapter<Any>,
        pagingEntity: PagingEntity<Any, Any, Any>
    ) = SelectionListScreenEntityFactory(
        mapper,
        filterCreator,
        prefetchCheckFunction,
        stubContentProvider,
        pagingEntity
    )

    @Provides
    @SelectionListScreenScope
    fun provideEntity(
        entityFactory: SelectionListScreenEntityFactory<Any, Any, Any>
    ) = entityFactory.createEntity()

    @Provides
    @SelectionListScreenScope
    fun provideStubContentProvider(
        arguments: Bundle,
        selectorStrings: SelectorStrings
    ): StubContentProviderAdapter<Any> =
        StubContentProviderAdapter(resolveStubContentProvider(arguments.customStubContentProvider, selectorStrings))

    @Provides
    @SelectionListScreenScope
    @Suppress("UNCHECKED_CAST" /* безопасность типа обеспечена на этапе создания компонента */)
    fun providerPrefetchCheckFunction(arguments: Bundle): PrefetchCheckFunction<SelectorItemModel> =
        arguments.prefetchCheckFunction as PrefetchCheckFunction<SelectorItemModel>
}