package ru.tensor.sbis.richtext

import android.content.Context
import ru.tensor.sbis.common.util.theme.SbisThemedContextFactory
import ru.tensor.sbis.toolbox_decl.linkopener.service.DecoratedLinkFeature
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import ru.tensor.sbis.richtext.contract.RichTextDependency
import ru.tensor.sbis.toolbox_decl.linkopener.service.LinkDecoratorServiceRepository
import ru.tensor.sbis.design.R as RDesign

/**
 * Плагин модуля "Богатый текст"
 *
 * @property component зависимости модуля от других сущностей
 *
 * @author us.bessonov
 */
object RichTextPlugin : BasePlugin<Unit>() {

    @JvmField
    internal val component = object : RichTextDependency {
        override val decoratedLinkServiceRepository: LinkDecoratorServiceRepository?
            get() = decoratedLinkFeature?.get()?.linkDecoratorServiceRepository
    }

    private var decoratedLinkFeature: FeatureProvider<DecoratedLinkFeature>? = null

    override val api = emptySet<FeatureWrapper<out Feature>>()

    override val dependency = Dependency.Builder()
        .optional(DecoratedLinkFeature::class.java) { decoratedLinkFeature = it }
        .build()

    override val customizationOptions = Unit

    /**
     * Возвращает темизированный контекст на основе переданного.
     */
    @JvmStatic
    fun themedContext(base: Context): SbisThemedContext {
        return if (base is SbisThemedContext) {
            return base
        } else {
            themedAppContext ?: SbisThemedContextFactory.create(
                base.applicationContext,
                RDesign.style.DefaultLightTheme,
                RDesign.style.BaseAppTheme
            ).also {
                themedAppContext = it
            }
        }
    }
}