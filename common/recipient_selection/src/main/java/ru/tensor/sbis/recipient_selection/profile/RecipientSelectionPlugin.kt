package ru.tensor.sbis.recipient_selection.profile

import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.person_decl.employee.person_card.factory.PersonCardIntentFactory
import ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionProvider
import ru.tensor.sbis.communication_decl.recipient_selection.RecipientSelectionResultManagerProviderContract
import ru.tensor.sbis.design.profile.person.feature.requirePersonViewComponent
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import ru.tensor.sbis.recipient_selection.profile.contract.RecipientSelectionDependency
import ru.tensor.sbis.recipient_selection.profile.contract.RecipientSelectionFeature
import ru.tensor.sbis.recipient_selection.profile.contract.RecipientSelectionFeatureImpl
import ru.tensor.sbis.recipient_selection.profile.contract.RecipientSelectionResultManagerProvider
import ru.tensor.sbis.recipient_selection.profile.contract.RepostRecipientSelectionResultManagerProvider
import ru.tensor.sbis.recipient_selection.profile.di.RecipientSelectionSingletonComponent
import ru.tensor.sbis.recipient_selection.profile.di.RecipientSelectionSingletonComponentInitializer

/**
 * Плагин модуля выбора получетелей.
 *
 * @author vv.chekurda
 */
object RecipientSelectionPlugin : BasePlugin<Unit>() {

    private lateinit var commonSingletonComponentProvider: FeatureProvider<CommonSingletonComponent>
    private lateinit var employeeProfileControllerWrapperProvider: FeatureProvider<EmployeeProfileControllerWrapper.Provider>
    private var personCardIntentProvider: FeatureProvider<PersonCardIntentFactory>? = null

    private val recipientSelectionFeature: RecipientSelectionFeature by lazy {
        RecipientSelectionFeatureImpl()
    }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(RecipientSelectionProvider::class.java) { recipientSelectionFeature },
        FeatureWrapper(RecipientSelectionResultManagerProvider::class.java) { recipientSelectionFeature },
        FeatureWrapper(RecipientSelectionResultManagerProviderContract::class.java) { recipientSelectionFeature },
        FeatureWrapper(RepostRecipientSelectionResultManagerProvider::class.java) { recipientSelectionFeature },
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(CommonSingletonComponent::class.java) { commonSingletonComponentProvider = it }
        .require(EmployeeProfileControllerWrapper.Provider::class.java) { employeeProfileControllerWrapperProvider = it }
        .optional(PersonCardIntentFactory::class.java) { personCardIntentProvider = it }
        .requirePersonViewComponent()
        .build()

    override val customizationOptions: Unit = Unit

    internal val singletonComponent: RecipientSelectionSingletonComponent by lazy {
        RecipientSelectionSingletonComponentInitializer(
            object : RecipientSelectionDependency,
                EmployeeProfileControllerWrapper.Provider by employeeProfileControllerWrapperProvider.get() {
                override val personCardIntentFactory: PersonCardIntentFactory? = personCardIntentProvider?.get()
            }
        ).init(commonSingletonComponentProvider.get())
    }
}