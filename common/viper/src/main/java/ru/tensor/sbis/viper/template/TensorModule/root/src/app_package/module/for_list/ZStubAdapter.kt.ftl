package ${packageName}.presentation.adapter

import android.view.ViewGroup
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.base_components.adapter.BaseTwoWayPaginationAdapter
import ${packageName}.presentation.adapter.holder.${modelName}Holder
import ${commonNamespace}.model.${modelName}

class ${modelName}Adapter : BaseTwoWayPaginationAdapter<${modelName}>() {

    init {
        mShowOlderLoadingProgress = false
        mWithBottomEmptyHolder = false
    }

    override fun getItemType(dataModel: ${modelName}?): Int = HOLDER_EMPTY

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<${modelName}> =
            ${modelName}Holder(parent)

    override fun onBindViewHolder(holder: AbstractViewHolder<${modelName}>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.bind(content[position])
    }
}