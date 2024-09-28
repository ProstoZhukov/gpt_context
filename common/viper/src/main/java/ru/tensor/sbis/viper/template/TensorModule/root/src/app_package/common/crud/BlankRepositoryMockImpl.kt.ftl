package ${commonNamespace}.crud.${moduleName}

import ${commonNamespace}.generated.ListResultOf${modelName}
import ${commonNamespace}.generated.${modelName}
import ${commonNamespace}.generated.${modelName}Filter
import ru.tensor.sbis.crud.generated.DataRefreshCallback
import ru.tensor.sbis.crud.generated.Subscription
import ${commonNamespace}.crud.StubSubscription
import ${commonNamespace}.crud.${moduleName}.mocks.Beans
import java.util.*

internal class ${modelName}RepositoryMockImpl() :
        ${modelName}Repository {

    private val mocks = Beans()
<#if includeCrud>

    override fun create(): ${modelName} = mocks.create()
    override fun update(entity: ${modelName}): ${modelName} = entity
    override fun delete(uuid: UUID): Boolean = mocks.delete(uuid)
    override fun read(uuid: UUID): ${modelName} = mocks.read(uuid)
    override fun readFromCache(uuid: UUID): ${modelName} = read(uuid)
</#if>
<#if includeList>

    override fun list(filter: ${modelName}Filter): ListResultOf${modelName} = mocks.list(filter)
    override fun refresh(filter: ${modelName}Filter): ListResultOf${modelName} = mocks.list(filter)
    override fun setDataRefreshCallback(callback: DataRefreshCallback): Subscription = StubSubscription.stub()	
</#if>
}