package ${commonNamespace}.crud.${moduleName}

import ${commonNamespace}.generated.*
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.crud.generated.Subscription
import ru.tensor.sbis.crud.generated.DataRefreshCallback
import java.util.*

internal class ${modelName}RepositoryImpl(private val controller: DependencyProvider<${modelName}Manager>) :
        ${modelName}Repository {
<#if includeCrud>

    override fun create(): ${modelName} =
            controller.get().create() ?: ${modelName}()

    override fun update(entity: ${modelName}): ${modelName} =
            controller.get().update(entity) ?: ${modelName}()

    override fun delete(uuid: UUID): Boolean =
            controller.get().delete(uuid)

    override fun read(uuid: UUID): ${modelName} =
            controller.get().read(uuid) ?: ${modelName}()

    override fun readFromCache(uuid: UUID): ${modelName} =
            read(uuid)
</#if>
<#if includeList>

    override fun list(filter: ${modelName}Filter): ListResultOf${modelName} =
            controller.get().list(filter)

    override fun refresh(filter: ${modelName}Filter): ListResultOf${modelName} =
            controller.get().refresh(filter)

    override fun setDataRefreshCallback(callback: DataRefreshCallback): Subscription =
            controller.get().setDataRefreshCallback(callback)		
</#if>
}