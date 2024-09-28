package ${commonNamespace}.model

import ru.tensor.sbis.common.util.UUIDUtils
import java.util.*
import ${commonNamespace}.generated.${modelName} as Controller${modelName}

data class ${modelName}(val uuid: UUID) {

    companion object {
        fun stub(): ${modelName} = ${modelName}(UUIDUtils.NIL_UUID)
    }
}

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun Controller${modelName}.map(): ${modelName} = ${modelName}(
        uuid)

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun ${modelName}.map(): Controller${modelName} = Controller${modelName}(
        uuid)
