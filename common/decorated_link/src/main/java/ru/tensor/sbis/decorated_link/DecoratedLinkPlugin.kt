package ru.tensor.sbis.decorated_link

import ru.tensor.sbis.linkdecorator.generated.LinkDecoratorService
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.toolbox_decl.linkopener.service.DecoratedLinkFeature

/**
 * Плагин модуля
 *
 * @author us.bessonov
 */
object DecoratedLinkPlugin : BasePlugin<Unit>() {

    private val feature = object : DecoratedLinkFeature {
        override val linkDecoratorServiceRepository by lazy {
            LinkDecoratorServiceRepositoryImpl(LinkDecoratorService.instance())
        }
    }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(DecoratedLinkFeature::class.java) { feature }
    )

    override val dependency = Dependency.EMPTY

    override val customizationOptions = Unit

}