package ${commonNamespace}.crud.${moduleName}

import ru.tensor.sbis.mvp.interactor.crudinterface.filter.AnchorPositionQueryBuilder
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.ListFilter
import java.io.Serializable
import ${commonNamespace}.generated.${modelName}Filter

class ${modelName}ListFilter : Serializable, ListFilter() {

    override fun queryBuilder(): Builder<*, *> =
            ${modelName}FilterBuilder()
                    .searchQuery(mSearchQuery)

    private class ${modelName}FilterBuilder
    internal constructor() :
            AnchorPositionQueryBuilder<Any, ${modelName}Filter>() {

        override fun build(): ${modelName}Filter =
                ${modelName}Filter()
    }
}