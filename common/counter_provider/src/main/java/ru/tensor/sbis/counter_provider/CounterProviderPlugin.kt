package ru.tensor.sbis.counter_provider

import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.toolbox_decl.counters.CountersSubscriptionProvider

/**
 * Плагин модуля.
 *
 * @author us.bessonov
 */
object CounterProviderPlugin : BasePlugin<Unit>() {

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(CountersSubscriptionProvider::class.java) {
            CountersSubscriptionProviderImpl(CountersRepository())
        }
    )

    override val dependency = Dependency.EMPTY

    override val customizationOptions = Unit
}