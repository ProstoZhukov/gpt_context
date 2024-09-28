package ru.tensor.sbis.communicator.communicator_host

import ru.tensor.sbis.communicator.common.data.model.CommunicatorHostFragmentFactory
import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorHostRouter
import ru.tensor.sbis.communicator.communicator_host.contract.CommunicatorHostDependency
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.Plugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин модуля communicator_host.
 *
 * @author da.zhukov
 */
object CommunicatorHostPlugin : Plugin<CommunicatorHostPlugin.CustomizationOptions> {

    private lateinit var communicatorHostRouterProvider: FeatureProvider<CommunicatorHostRouter.Provider>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(CommunicatorHostFragmentFactory::class.java) { CommunicatorHostFacade }
    )
    override val dependency: Dependency = Dependency.Builder()
        .require(CommunicatorHostRouter.Provider::class.java) { communicatorHostRouterProvider = it }
        .build()

    override val customizationOptions = CustomizationOptions()

    override fun initialize() {
        val dependency = object : CommunicatorHostDependency,
            CommunicatorHostRouter.Provider by communicatorHostRouterProvider.get() {}

        CommunicatorHostFacade.configure(dependency)
    }

    /**
     * Опции плагина модуля навигации
     */
    class CustomizationOptions internal constructor() {

        /**
         * Навигация с сохранением фраментов.
         * Необходимо выставлять если приложение использует стратегию отличающуюся от [NonCacheFragmentInstallationStrategy].
         */
        var navigationWithCachedFragment: Boolean = false
    }
}