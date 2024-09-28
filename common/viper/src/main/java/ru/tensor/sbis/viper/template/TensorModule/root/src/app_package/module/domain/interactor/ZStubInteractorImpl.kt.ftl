package ${packageName}.domain.interactor

import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ${packageName}.contract.internal.${modelName}Interactor
<#if includeList>
import ${commonNamespace}.crud.${moduleName}.${modelName}CommandWrapper

internal class ${modelName}InteractorImpl(override val commandWrapper: ${modelName}CommandWrapper) :
<#elseif includeCrud>

internal class ${modelName}InteractorImpl() :
</#if>
        BaseInteractor(),
        ${modelName}Interactor