package ${commonNamespace}.crud.${moduleName}.mapper

import android.content.Context
import ${commonNamespace}.generated.ListResultOf${modelName}
import ru.tensor.sbis.common.data.mapper.base.BaseItemMapper
import PagedListResult
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ${commonNamespace}.model.map
import ${commonNamespace}.model.${modelName}

internal class ${modelName}ListMapper(context: Context,
                                   private val baseItemMapper: BaseItemMapper) :
        BaseModelMapper<ListResultOf${modelName}, PagedListResult<${modelName}>>(context) {

    override fun apply(rawList: ListResultOf${modelName}): PagedListResult<${modelName}> =
            PagedListResult(rawList.result.map { it.map() }, rawList.haveMore)
}