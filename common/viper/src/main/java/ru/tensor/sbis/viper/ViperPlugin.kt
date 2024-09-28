package ru.tensor.sbis.viper

import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.viper.informer.GlobalInformerInterface
import ru.tensor.sbis.viper.informer.GlobalInformerInterfaceImpl

/**
 * Плагин для экрана каталога
 */
object ViperPlugin : BasePlugin<Unit>() {

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(GlobalInformerInterface::class.java) {
            GlobalInformerInterfaceImpl(application.baseContext)
        }
    )

    override val dependency: Dependency = Dependency.Builder()
        .build()

    override val customizationOptions: Unit = Unit
}