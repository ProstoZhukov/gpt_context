package ${packageName}.presentation.adapter.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ${packageName}.R
import ${commonNamespace}.model.${modelName}

class ${modelName}Holder(itemView: View) : AbstractViewHolder<${modelName}>(itemView) {

    constructor(parent: ViewGroup) :
            this(LayoutInflater.from(parent.context).inflate(R.layout.${moduleName}_item, parent, false) as View)

    override fun bind(model: ${modelName}) {
        super.bind(model)

        with(model) {
        }
    }
}