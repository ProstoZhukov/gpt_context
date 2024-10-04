package ru.tensor.sbis.design.link_share

import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.design.link_share.contract.LinkShareFeature
import ru.tensor.sbis.design.link_share.di.LinkShareComponent
import ru.tensor.sbis.design.link_share.di.LinkShareComponentInitializer
import ru.tensor.sbis.link_share.ui.LinkShareFragmentProvider
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин для экрана карточки отзыва
 */
object LinkSharePlugin : BasePlugin<Unit>() {

    /** @SelfDocumented */
    internal val singletonComponent: LinkShareComponent by lazy {
        LinkShareComponentInitializer(
            commonSingletonComponentProvider.get()
        )
            .init()
    }

    private lateinit var commonSingletonComponentProvider: FeatureProvider<CommonSingletonComponent>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(LinkShareFragmentProvider::class.java) { LinkShareFeature() },
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(CommonSingletonComponent::class.java) { commonSingletonComponentProvider = it }
        .build()

    override val customizationOptions: Unit = Unit
}