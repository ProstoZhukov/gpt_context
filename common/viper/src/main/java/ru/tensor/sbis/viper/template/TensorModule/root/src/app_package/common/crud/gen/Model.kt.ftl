package ${commonNamespace}.generated

import ru.tensor.sbis.common.util.UUIDUtils
import java.util.*

data class ${modelName}(val id: UUID) {
    constructor() : this(UUIDUtils.NIL_UUID)

    companion object {
        fun stub(): ${modelName} = ${modelName}(UUIDUtils.NIL_UUID)
    }
}
