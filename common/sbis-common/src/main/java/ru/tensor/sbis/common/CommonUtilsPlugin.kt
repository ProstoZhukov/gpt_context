package ru.tensor.sbis.common

import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.common.di.CommonSingletonComponentInitializer
import ru.tensor.sbis.common.feature.AndroidSystem
import ru.tensor.sbis.common.rx.RxBus
import ru.tensor.sbis.common.util.AlternativeNetworkUtils
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext

/**
 * Плагин с общими утилитами
 *
 * @author kv.martyshenko
 */
object CommonUtilsPlugin : BasePlugin<Unit>() {

    /* Public, т.к. используется напрямую в ContactsMainScreenAddon.kt */
    val singletonComponent: CommonSingletonComponent by lazy {
        CommonSingletonComponentInitializer()
            .init(themedAppContext ?: application, themedAppContext ?: SbisThemedContext(application))
    }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(CommonSingletonComponent::class.java) { singletonComponent },
        FeatureWrapper(NetworkUtils::class.java) { singletonComponent.networkUtils },
        FeatureWrapper(AlternativeNetworkUtils::class.java) { singletonComponent.alternativeNetworkUtils },
        FeatureWrapper(ResourceProvider::class.java) { singletonComponent.resourceProvider },
        FeatureWrapper(AndroidSystem::class.java) { singletonComponent.androidSystem },
        FeatureWrapper(ScrollHelper::class.java) { singletonComponent.scrollHelper },
        FeatureWrapper(RxBus::class.java) { singletonComponent.rxBus }
    )

    override val dependency: Dependency = Dependency.Builder().build()

    override val customizationOptions: Unit = Unit
}