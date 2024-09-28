package ${commonNamespace}.generated

import ru.tensor.sbis.crud.generated.DataRefreshCallback
import ru.tensor.sbis.crud.generated.Subscription
import java.util.*

class ${modelName}Manager {
<#if includeCrud>
    fun create(): ${modelName}? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun update(entity: ${modelName}): ${modelName}? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun delete(uuid: UUID): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun read(uuid: UUID): ${modelName}? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
</#if>
<#if includeList>

    fun list(filter: ${modelName}Filter): ListResultOf${modelName} {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun refresh(filter: ${modelName}Filter): ListResultOf${modelName} {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun setDataRefreshCallback(callback: DataRefreshCallback): Subscription {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
</#if>
}