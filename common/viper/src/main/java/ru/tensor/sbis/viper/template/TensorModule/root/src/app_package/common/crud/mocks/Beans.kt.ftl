package ${commonNamespace}.crud.${moduleName}.mocks

import ${commonNamespace}.generated.*
import java.util.*
import kotlin.collections.ArrayList

class Beans {

    private lateinit var mList: MutableList<${modelName}>
    val random = Random()

    init {
        mList = mutableListOf()
    }

    fun create(): ${modelName} {
        val wp = ${modelName}(UUID.randomUUID())
        mList.add(wp)
        return wp
    }

    fun delete(uuid: UUID): Boolean {
        return mList.removeAll { wp -> wp.uuid == uuid }
    }

    fun read(uuid: UUID): ${modelName} {
        return mList.singleOrNull { wp -> wp.uuid == uuid } ?: ${modelName}()
    }

    fun list(filter: ${modelName}Filter): ListResultOf${modelName} = ListResultOf${modelName}(ArrayList(mList), false, HashMap())
}
