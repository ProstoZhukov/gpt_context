package ru.tensor.sbis.communicator.crud

import ru.tensor.sbis.communicator.common.crud.ThemeRepositoryProvider
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин модуля communicator_crud
 *
 * @author da.zhukov
 */
object CommunicatorCrudPlugin : BasePlugin<Unit>() {

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(ThemeRepositoryProvider::class.java) { CommunicatorCrudFacade }
    )

    override val dependency: Dependency = Dependency.Builder()
        .build()

    override val customizationOptions: Unit = Unit
}