package ${packageName}.di

import ru.tensor.sbis.common.di.BaseSingletonComponentInitializer
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ${packageName}.contract.${moduleName}CommonDependency

class ${moduleName}CommonComponentInitializer(private val dependency: ${moduleName}CommonDependency,
                                         private val containerId: Int,
                                         private val subContainerId: Int) :
        BaseSingletonComponentInitializer<${moduleName}CommonComponent>() {

    override fun createComponent(commonSingletonComponent: CommonSingletonComponent): ${moduleName}CommonComponent =
            Dagger${moduleName}CommonComponent.builder()
                    .commonSingletonComponent(commonSingletonComponent)
                    .dependency(dependency)
                    .containerId(containerId)
                    .subContainerId(subContainerId)
                    .build()

    override fun initSingletons(singletonComponent: ${moduleName}CommonComponent) {

    }
}