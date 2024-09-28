package ${commonNamespace}.crud.${moduleName}.mapper

import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import android.content.Context
import ${commonNamespace}.model.${modelName}
import ${commonNamespace}.model.map
import ${commonNamespace}.generated.${modelName} as Controller${modelName}

class ${modelName}Mapper(context: Context) :
        BaseModelMapper<Controller${modelName}, ${modelName}>(context) {

    override fun apply(rawData: Controller${modelName}): ${modelName} =
            rawData.map()
}
