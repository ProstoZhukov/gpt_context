package ${packageName}.di

import ru.tensor.sbis.common.di.BaseSingletonComponentInitializer
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ${commonNamespace}.di.${commonModuleName}CommonComponent
import ${packageName}.contract.${modelName}Dependency

class ${modelName}ComponentInitializer(private val ${"${commonModuleName}"?lower_case}CommonComponent: ${commonModuleName}CommonComponent,
                                private val dependency: ${modelName}Dependency,
                                private val containerId: Int,
                                private val subContainerId: Int,
                                private val hostContainerId: Int) :
        BaseSingletonComponentInitializer<${modelName}Component>() {

    override fun createComponent(commonSingletonComponent: CommonSingletonComponent): ${modelName}Component {
        return Dagger${modelName}Component.builder()
                .${"${commonModuleName}"?lower_case}CommonComponent(${"${commonModuleName}"?lower_case}CommonComponent)
                .dependency(dependency)
                .containerId(containerId)
                .subContainerId(subContainerId)
                .hostContainerId(hostContainerId)
                .build()
    }

    override fun initSingletons(singletonComponent: ${modelName}Component) {

    }
}