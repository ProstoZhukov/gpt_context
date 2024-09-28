package ${commonNamespace}.di.repository

import ru.tensor.sbis.common.data.DependencyProvider
import PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.CreateObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ListObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ReadObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.UpdateObservableCommand
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.viper.crud.DeleteRepositoryCommand
import ${commonNamespace}.crud.${moduleName}.${modelName}CommandWrapper
<#if includeList>
import ${commonNamespace}.crud.${moduleName}.${modelName}ListFilter
</#if>
import ${commonNamespace}.crud.${moduleName}.${modelName}Repository
import ${commonNamespace}.model.${modelName}
import ${commonNamespace}.generated.${modelName} as Controller${modelName}
import ${commonNamespace}.generated.*

interface ${modelName}Component {

    fun get${modelName}Manager(): DependencyProvider<${modelName}Manager>
    fun get${modelName}Repository(): ${modelName}Repository
    fun get${modelName}CommandWrapper(): ${modelName}CommandWrapper
	
    fun get${modelName}Mapper(): BaseModelMapper<Controller${modelName}, ${modelName}>
 <#if includeList>
 
    fun get${modelName}ListCommand(): ListObservableCommand<PagedListResult<${modelName}>, ${modelName}Filter>
    fun get${modelName}ListMapper(): BaseModelMapper<ListResultOf${modelName}, PagedListResult<${modelName}>>
    fun get${modelName}ListFilter(): ${modelName}ListFilter
</#if>
 <#if includeCrud>
 
    fun get${modelName}CreateCommand(): CreateObservableCommand<Controller${modelName}>
    fun get${modelName}ReadCommand(): ReadObservableCommand<${modelName}>
    fun get${modelName}UpdateCommand(): UpdateObservableCommand<Controller${modelName}>
    fun get${modelName}DeleteCommand(): DeleteRepositoryCommand<Controller${modelName}>
</#if>
}