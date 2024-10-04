package ru.tensor.sbis.design.recipient_selection

import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communication_decl.selection.sources.edo.SelectionFacesSource
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionProvider
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultDelegate
import ru.tensor.sbis.communication_decl.selection.recipient.menu.RecipientSelectionMenuProvider
import ru.tensor.sbis.design.profile.person.feature.requirePersonViewComponent
import ru.tensor.sbis.design.profile_decl.person.PersonClickListener
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import ru.tensor.sbis.design.recipient_selection.contract.RecipientSelectionDependency
import ru.tensor.sbis.design.recipient_selection.contract.RecipientSelectionFeatureFacade
import ru.tensor.sbis.design.recipient_selection.ui.di.singleton.RecipientSelectionSingletonComponent
import ru.tensor.sbis.design.recipient_selection.ui.di.singleton.RecipientSelectionSingletonComponentInitializer

/**
 * Плагин модуля выбора получетелей.
 *
 * @author vv.chekurda
 */
object RecipientSelectionPlugin : BasePlugin<Unit>() {

    private lateinit var commonSingletonComponentProvider: FeatureProvider<CommonSingletonComponent>
    private lateinit var employeeProfileControllerWrapperProvider: FeatureProvider<EmployeeProfileControllerWrapper.Provider>
    private var personClickListener: FeatureProvider<PersonClickListener>? = null
    private var selectionFacesSourceProvider: FeatureProvider<SelectionFacesSource.Provider>? = null

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(RecipientSelectionProvider::class.java) { RecipientSelectionFeatureFacade },
        FeatureWrapper(RecipientSelectionMenuProvider::class.java) { RecipientSelectionFeatureFacade },
        FeatureWrapper(RecipientSelectionResultDelegate.Provider::class.java) { RecipientSelectionFeatureFacade }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(CommonSingletonComponent::class.java) { commonSingletonComponentProvider = it }
        .require(EmployeeProfileControllerWrapper.Provider::class.java) { employeeProfileControllerWrapperProvider = it }
        .optional(PersonClickListener::class.java) { personClickListener = it }
        .optional(SelectionFacesSource.Provider::class.java) { selectionFacesSourceProvider = it }
        .requirePersonViewComponent()
        .build()

    override val customizationOptions: Unit = Unit

    internal val singletonComponent: RecipientSelectionSingletonComponent by lazy {
        val dependency = object : RecipientSelectionDependency,
            EmployeeProfileControllerWrapper.Provider by employeeProfileControllerWrapperProvider.get() {

            override val personClickListener: PersonClickListener? =
                this@RecipientSelectionPlugin.personClickListener?.get()

            override val selectionFacesSource: SelectionFacesSource? =
                selectionFacesSourceProvider?.get()?.getSelectionFacesSource()
        }
        RecipientSelectionSingletonComponentInitializer(dependency).init(commonSingletonComponentProvider.get())
    }
}