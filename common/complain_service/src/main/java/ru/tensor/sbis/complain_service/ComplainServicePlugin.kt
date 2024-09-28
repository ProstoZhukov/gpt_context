package ru.tensor.sbis.complain_service

import ru.tensor.sbis.communication_decl.complain.ComplainDialogFragmentFeature
import ru.tensor.sbis.communication_decl.complain.ComplainService
import ru.tensor.sbis.complain_service.contract.ComplainServiceDependency
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.verification_decl.login.LoginInterface

/**
 * Плагин сервиса "Пожаловаться".
 *
 * @author da.zhukov
 */
object ComplainServicePlugin : BasePlugin<Unit>() {

    private lateinit var loginInterfaceProvider: FeatureProvider<LoginInterface.Provider>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(ComplainService.Provider::class.java) { ComplainServiceFeatureFacade },
        FeatureWrapper(ComplainDialogFragmentFeature::class.java) { ComplainServiceFeatureFacade }
    )

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .require(LoginInterface.Provider::class.java) { loginInterfaceProvider = it }
            .build()
    }

    override val customizationOptions = Unit

    override fun initialize() {
        val dependency = object : ComplainServiceDependency,
            LoginInterface.Provider by loginInterfaceProvider.get() {}
        ComplainServiceFeatureFacade.configure(dependency)
    }
}