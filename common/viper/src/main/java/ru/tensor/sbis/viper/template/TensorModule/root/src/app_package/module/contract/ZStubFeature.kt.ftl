package ${packageName}.contract

import androidx.fragment.app.Fragment
import ${packageName}.presentation.view.${modelName}Fragment
import ${commonNamespace}.feature.${moduleName}.ui.${modelName}FragmentProvider

class ${modelName}Feature:
        ${modelName}FragmentProvider {

    override fun get${modelName}Fragment(): Fragment =
            ${modelName}Fragment.newInstance()
}