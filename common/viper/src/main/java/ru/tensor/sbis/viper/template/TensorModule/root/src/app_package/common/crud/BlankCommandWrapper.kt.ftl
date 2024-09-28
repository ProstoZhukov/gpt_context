package ${commonNamespace}.crud.${moduleName}

import PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.CreateObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ListObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ReadObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.UpdateObservableCommand
import ru.tensor.sbis.viper.crud.DeleteRepositoryCommand
import ${commonNamespace}.model.${modelName}
import ${commonNamespace}.generated.${modelName} as Controller${modelName}
import ${commonNamespace}.generated.${modelName}Filter

/**
 * Wrapper команд для контроллера 
 */
interface ${modelName}CommandWrapper {

 <#if includeCrud>
    val createCommand: CreateObservableCommand<Controller${modelName}>
    val readCommand: ReadObservableCommand<${modelName}>
    val updateCommand: UpdateObservableCommand<Controller${modelName}>
    val deleteCommand: DeleteRepositoryCommand<Controller${modelName}>
</#if>
 <#if includeList>
    val listCommand: ListObservableCommand<PagedListResult<${modelName}>, ${modelName}Filter>
</#if>
}