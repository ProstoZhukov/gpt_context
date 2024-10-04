package ru.tensor.sbis.design.period_picker

import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature
import ru.tensor.sbis.design.period_picker.feature.SbisPeriodPickerFeatureImpl
import ru.tensor.sbis.design.period_picker.decl.SbisCompactPeriodPickerFragmentFeature
import ru.tensor.sbis.design.period_picker.feature.SbisCompactPeriodPickerFragmentFeatureImpl
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин компонента Выбор периода.
 *
 * @author mb.kruglova
 */
object SbisPeriodPickerPlugin : BasePlugin<Unit>() {
    internal var resourceProvider: FeatureProvider<ResourceProvider>? = null

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(SbisPeriodPickerFeature::class.java) { SbisPeriodPickerFeatureImpl() },
        FeatureWrapper(SbisCompactPeriodPickerFragmentFeature::class.java) {
            SbisCompactPeriodPickerFragmentFeatureImpl()
        }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(ResourceProvider::class.java) { resourceProvider = it }
        .build()
    override val customizationOptions: Unit = Unit
}