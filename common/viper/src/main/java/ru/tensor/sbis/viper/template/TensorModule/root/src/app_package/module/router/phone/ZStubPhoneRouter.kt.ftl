package ${packageName}.presentation.router.phone

import ru.tensor.sbis.declaration.AndroidComponent
import ru.tensor.sbis.viper.arch.router.AbstractPhoneRouter
import ${packageName}.contract.${modelName}Dependency
import ${packageName}.contract.internal.${modelName}Router

internal class ${modelName}PhoneRouter(private val dependency: ${modelName}Dependency,
                                androidComponent: AndroidComponent,
                                containerId: Int) :
        AbstractPhoneRouter(androidComponent, containerId),
        ${modelName}Router