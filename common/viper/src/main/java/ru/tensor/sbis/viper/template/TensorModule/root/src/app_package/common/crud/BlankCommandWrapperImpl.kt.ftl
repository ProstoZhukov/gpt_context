package ${commonNamespace}.crud.${moduleName}

import PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.CreateObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ListObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ReadObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.UpdateObservableCommand
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.viper.crud.DeleteRepositoryCommand
import ${commonNamespace}.model.${modelName}
import ${commonNamespace}.generated.${modelName} as Controller${modelName}
import ${commonNamespace}.generated.${modelName}Filter

<#if includeCrud && includeList>
internal class ${modelName}CommandWrapperImpl(override val createCommand: CreateObservableCommand<Controller${modelName}>,
                                           override val readCommand: ReadObservableCommand<${modelName}>,
                                           override val updateCommand: UpdateObservableCommand<Controller${modelName}>,
                                           override val deleteCommand: DeleteRepositoryCommand<Controller${modelName}>,
                                           override val listCommand: ListObservableCommand<PagedListResult<${modelName}>, ${modelName}Filter>) :
<#elseif includeCrud && !includeList>
internal class ${modelName}CommandWrapperImpl(override val createCommand: CreateObservableCommand<Controller${modelName}>,
                                           override val readCommand: ReadObservableCommand<${modelName}>,
                                           override val updateCommand: UpdateObservableCommand<Controller${modelName}>,
                                           override val deleteCommand: DeleteRepositoryCommand<Controller${modelName}>) :
<#elseif !includeCrud && includeList>   
internal class ${modelName}CommandWrapperImpl(override val listCommand: ListObservableCommand<PagedListResult<${modelName}>, ${modelName}Filter>) :
 </#if>										   
        ${modelName}CommandWrapper,
        BaseInteractor()