package ${packageName}.presentation.router.tablet

import ru.tensor.sbis.declaration.AndroidComponent
import ru.tensor.sbis.viper.arch.router.AbstractTabletRouter
import ${packageName}.contract.${modelName}Dependency
import ${packageName}.contract.internal.${modelName}Router

internal class ${modelName}TabletRouter(private val dependency: ${modelName}Dependency,
                                 androidComponent: AndroidComponent,
                                 hostContainerId: Int,
                                 containerId: Int,
                                 subContainerId: Int) :
        AbstractTabletRouter(androidComponent, hostContainerId, containerId, subContainerId),
        ${modelName}Router