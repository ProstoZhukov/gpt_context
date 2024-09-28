package ru.tensor.sbis.red_button

import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.verification_decl.red_button.RedButtonActivatedProvider
import ru.tensor.sbis.login.verification.contract.VerificationFeature
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.red_button.di.RedButtonComponent
import ru.tensor.sbis.red_button.feature.RedButtonFeature

/**
 * Плагин RedButton
 *
 * @author kv.martyshenko
 */
object RedButtonPlugin : BasePlugin<Unit>() {

    internal lateinit var commonSingletonComponentProvider: FeatureProvider<CommonSingletonComponent>
    private lateinit var verificationFeatureProvider: FeatureProvider<VerificationFeature>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(RedButtonActivatedProvider::class.java) { redButtonComponent.redButtonFeature },
        FeatureWrapper(RedButtonFeature::class.java) { redButtonComponent.redButtonFeature }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(CommonSingletonComponent::class.java) { commonSingletonComponentProvider = it }
        .require(VerificationFeature::class.java) { verificationFeatureProvider = it }
        .build()

    override val customizationOptions: Unit = Unit

    override fun doAfterInitialize() {
        redButtonComponent.redButtonFeature.subscribeOnRedButtonControllerCallback()
    }

    internal val redButtonComponent: RedButtonComponent by lazy {
        val dependency = object : RedButtonDependency,
            VerificationFeature by verificationFeatureProvider.get() {

        }
        RedButtonComponent.Initializer.init(commonSingletonComponentProvider.get(), dependency)
    }

}