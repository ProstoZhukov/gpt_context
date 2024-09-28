package ${commonNamespace}.crud.${moduleName}

import ru.tensor.sbis.mvp.interactor.crudinterface.CRUDRepository
import ru.tensor.sbis.mvp.interactor.crudinterface.ListRepository
import ${commonNamespace}.generated.ListResultOf${modelName}
import ${commonNamespace}.generated.${modelName}
import ${commonNamespace}.generated.${modelName}Filter

/**
 * Интерфейс для связи с контроллером.
 */
interface ${modelName}Repository 
<#if includeCrud && !includeList>
        : CRUDRepository<${modelName}>
<#elseif !includeCrud && includeList>
        : ListRepository<ListResultOf${modelName}, ${modelName}Filter>
<#elseif includeCrud && includeList>
        : CRUDRepository<${modelName}>,
        ListRepository<ListResultOf${modelName}, ${modelName}Filter>
</#if>