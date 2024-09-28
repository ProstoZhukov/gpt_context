package ru.tensor.sbis.plugin_manager.resolver

import ru.tensor.sbis.plugin_manager.PluginManager
import ru.tensor.sbis.plugin_struct.Plugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider

typealias ResolveAction<OUTPUT> = (
    featureType: Class<out Feature>,
    caller: Plugin<*>,
    records: Set<PluginManager.Record<out Feature>>,
    fallback: (featureType: Class<out Feature>, caller: Plugin<*>, records: Set<PluginManager.Record<out Feature>>) -> OUTPUT
) -> OUTPUT

/**
 * Реализация [FeatureResolver], позволяющая гибче подстраивать поведение.
 *
 * @author kv.martyshenko
 */
class FlexibleFeatureResolver(
    private val fallbackResolver: FeatureResolver = SimpleFeatureResolver(),
    private val onResolveRequiredSingle: ResolveAction<FeatureProvider<out Feature>> = { featureType, caller, records, fallback-> fallback(featureType, caller, records) },
    private val onResolveOptionalSingle: ResolveAction<FeatureProvider<out Feature>?> = { featureType, caller, records, fallback-> fallback(featureType, caller, records) },
    private val onResolveRequiredMulti: ResolveAction<Set<FeatureProvider<out Feature>>> = { featureType, caller, records, fallback-> fallback(featureType, caller, records) },
    private val onResolveOptionalMulti: ResolveAction<Set<FeatureProvider<out Feature>>?> = { featureType, caller, records, fallback-> fallback(featureType, caller, records) }
) : FeatureResolver {

    override fun resolveRequiredSingle(
        featureType: Class<out Feature>,
        caller: Plugin<*>,
        records: Set<PluginManager.Record<out Feature>>
    ): FeatureProvider<out Feature> {
        return onResolveRequiredSingle(featureType, caller, records, fallbackResolver::resolveRequiredSingle)
    }

    override fun resolveOptionalSingle(
        featureType: Class<out Feature>,
        caller: Plugin<*>,
        records: Set<PluginManager.Record<out Feature>>
    ): FeatureProvider<out Feature>? {
        return onResolveOptionalSingle(featureType, caller, records, fallbackResolver::resolveOptionalSingle)
    }

    override fun resolveRequiredMulti(
        featureType: Class<out Feature>,
        caller: Plugin<*>,
        records: Set<PluginManager.Record<out Feature>>
    ): Set<FeatureProvider<out Feature>> {
        return onResolveRequiredMulti(featureType, caller, records, fallbackResolver::resolveRequiredMulti)
    }

    override fun resolveOptionalMulti(
        featureType: Class<out Feature>,
        caller: Plugin<*>,
        records: Set<PluginManager.Record<out Feature>>
    ): Set<FeatureProvider<out Feature>>? {
        return onResolveOptionalMulti(featureType, caller, records, fallbackResolver::resolveOptionalMulti)
    }
}