package ru.tensor.sbis.communicator_support_consultation_list.di

import ru.tensor.sbis.communicator_support_consultation_list.feature.SupportConsultationListFragmentFactory
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин для создания фабрики фрагмнента с обращениями в  поддержку
 * @see SupportConsultationListFragmentFactory
 */
object SupportConsultationListPlugin : BasePlugin<Unit>() {

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(SupportConsultationListFragmentFactory::class.java) { component.getFeature() }
    )

    override val dependency: Dependency = Dependency.EMPTY

    override val customizationOptions: Unit = Unit

    internal val component: SupportRequestsListComponent by lazy {
        DaggerSupportRequestsListComponent.create()
    }
}