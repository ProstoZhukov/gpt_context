package ${commonNamespace}.di.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.data.mapper.base.BaseItemMapper
import PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.*
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ${commonNamespace}.BuildConfig
import ru.tensor.sbis.viper.crud.DeleteRepositoryCommand
import ru.tensor.sbis.viper.crud.DeleteRepositoryCommandImpl
import ${commonNamespace}.crud.${moduleName}.*
<#if includeList>
import ${commonNamespace}.crud.${moduleName}.mapper.${modelName}ListMapper
</#if>
import ${commonNamespace}.crud.${moduleName}.mapper.${modelName}Mapper
import ${commonNamespace}.di.${commonModuleName}CommonScope
import ${commonNamespace}.model.${modelName}
import ${commonNamespace}.generated.${modelName} as Controller${modelName}
import ${commonNamespace}.generated.*

@Module
class ${modelName}Module {

    @Provides
    @${commonModuleName}CommonScope
    internal fun provideFacade(service: DependencyProvider<AppService>):
            DependencyProvider<${modelName}Manager> =
<#if includeGen>
            DependencyProvider.create { ${modelName}Manager() }
<#else>
			DependencyProvider.create { service.get().${moduleName}Manager() }
</#if>

    @Provides
    @${commonModuleName}CommonScope
    @Suppress("ConstantConditionIf")
    internal fun provideRepository(manager: DependencyProvider<${modelName}Manager>):
            ${modelName}Repository =
            if (BuildConfig.MOCK_SETTINGS)
                ${modelName}RepositoryMockImpl()
            else
                ${modelName}RepositoryImpl(manager)

 <#if includeCrud && includeList>
    @Provides
    @${commonModuleName}CommonScope
    internal fun provideCommandWrapper(createCommand: CreateObservableCommand<Controller${modelName}>,
                                       readCommand: ReadObservableCommand<${modelName}>,
                                       updateCommand: UpdateObservableCommand<Controller${modelName}>,
                                       deleteCommand: DeleteRepositoryCommand<Controller${modelName}>,
                                       listCommand: ListObservableCommand<PagedListResult<${modelName}>, ${modelName}Filter>):
            ${modelName}CommandWrapper =
            ${modelName}CommandWrapperImpl(createCommand, readCommand, updateCommand, deleteCommand, listCommand)
<#elseif includeCrud && !includeList>
    @Provides
    @${commonModuleName}CommonScope
    internal fun provideCommandWrapper(createCommand: CreateObservableCommand<Controller${modelName}>,
                                       readCommand: ReadObservableCommand<${modelName}>,
                                       updateCommand: UpdateObservableCommand<Controller${modelName}>,
                                       deleteCommand: DeleteRepositoryCommand<Controller${modelName}>):
            ${modelName}CommandWrapper =
            ${modelName}CommandWrapperImpl(createCommand, readCommand, updateCommand, deleteCommand)
<#elseif !includeCrud && includeList>
    @Provides
    @${commonModuleName}CommonScope
	internal fun provideCommandWrapper(listCommand: ListObservableCommand<PagedListResult<${modelName}>, ${modelName}Filter>):
            ${modelName}CommandWrapper =
            ${modelName}CommandWrapperImpl(listCommand)
 </#if>

<#if includeCrud>
   
    @Provides
    @${commonModuleName}CommonScope
    internal fun provideMapper(context: Context):
            BaseModelMapper<Controller${modelName}, ${modelName}> =
            ${modelName}Mapper(context)
   
    @Provides
    @${commonModuleName}CommonScope
    internal fun provideCreateCommand(repository: ${modelName}Repository):
            CreateObservableCommand<Controller${modelName}> =
            CreateCommand(repository)

    @Provides
    @${commonModuleName}CommonScope
    internal fun provideReadCommand(repository: ${modelName}Repository,
                                    mapper: BaseModelMapper<Controller${modelName}, ${modelName}>):
            ReadObservableCommand<${modelName}> =
            ReadCommand<${modelName}, Controller${modelName}>(repository, mapper)

    @Provides
    @${commonModuleName}CommonScope
    internal fun provideUpdateCommand(repository: ${modelName}Repository):
            UpdateObservableCommand<Controller${modelName}> =
            UpdateCommand(repository)

    @Provides
    @${commonModuleName}CommonScope
    internal fun provideDeleteCommand(repository: ${modelName}Repository):
            DeleteRepositoryCommand<Controller${modelName}> =
            DeleteRepositoryCommandImpl(repository)
</#if>

<#if includeList>
    @Provides
    internal fun provideFilter():
            ${modelName}ListFilter =
            ${modelName}ListFilter()
   
    @Provides
    @${commonModuleName}CommonScope
    internal fun provideListCommand(repository: ${modelName}Repository,
                                    mapper: BaseModelMapper<ListResultOf${modelName}, PagedListResult<${modelName}>>):
            ListObservableCommand<PagedListResult<${modelName}>, ${modelName}Filter> =
            ListCommand(repository, mapper)
			
	@Provides
    @${commonModuleName}CommonScope
    internal fun provideListMapper(context: Context,
                                   baseItemMapper: BaseItemMapper):
            BaseModelMapper<ListResultOf${modelName}, PagedListResult<${modelName}>> =
            ${modelName}ListMapper(context, baseItemMapper)
</#if>
}