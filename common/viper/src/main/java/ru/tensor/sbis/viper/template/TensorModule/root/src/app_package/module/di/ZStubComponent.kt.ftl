package ${packageName}.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.common.util.NetworkUtils
import ${commonNamespace}.di.${commonModuleName}CommonComponent
import ${packageName}.contract.${modelName}Dependency
import ${packageName}.contract.${modelName}Feature
import javax.inject.Named
import ${commonNamespace}.di.repository.${modelName}Component as Common${modelName}Component

@${modelName}Scope
@Component(dependencies = [(${commonModuleName}CommonComponent::class)],
        modules = [(${modelName}Module::class)])
interface ${modelName}Component : Common${modelName}Component {

    fun getFeature(): ${modelName}Feature
    fun getDependency(): ${modelName}Dependency

    @Named("hostContainerId")
    fun getHostContainerId(): Int

    @Named("containerId")
    fun getContainerId(): Int

    @Named("subContainerId")
    fun getSubContainerId(): Int

    // Common
    fun getContext(): Context

    fun getNetworkUtils(): NetworkUtils

    @Component.Builder
    interface Builder {

        fun ${"${commonModuleName}"?lower_case}CommonComponent(${"${commonModuleName}"?lower_case}CommonComponent: ${commonModuleName}CommonComponent): Builder

        @BindsInstance
        fun dependency(dependency: ${modelName}Dependency): Builder

        @BindsInstance
        fun containerId(@Named("containerId") containerId: Int): Builder

        @BindsInstance
        fun subContainerId(@Named("subContainerId") subContainerId: Int): Builder

        @BindsInstance
        fun hostContainerId(@Named("hostContainerId") hostContainerId: Int): Builder

        fun build(): ${modelName}Component
    }
}