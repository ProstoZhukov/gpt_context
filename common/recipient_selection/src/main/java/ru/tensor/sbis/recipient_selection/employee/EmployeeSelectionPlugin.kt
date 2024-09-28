package ru.tensor.sbis.recipient_selection.employee

import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communication_decl.employeeselection.EmployeesSelectionProvider
import ru.tensor.sbis.communication_decl.employeeselection.EmployeesSelectionResultManagerProvider
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.profile_service.models.employee.EmployeesControllerWrapper
import ru.tensor.sbis.recipient_selection.employee.contract.EmployeeSelectionDependency
import ru.tensor.sbis.recipient_selection.employee.contract.EmployeeSelectionFeature
import ru.tensor.sbis.recipient_selection.employee.contract.EmployeeSelectionFeatureImpl
import ru.tensor.sbis.recipient_selection.employee.di.EmployeeSelectionSingletonComponentInitializer
import ru.tensor.sbis.recipient_selection.employee.di.MutableEmployeesSelectionResultManagerProvider
import ru.tensor.sbis.verification_decl.login.CurrentAccount

/**
 * Плагин выбора сотрудников
 */
object EmployeeSelectionPlugin : BasePlugin<Unit>() {

    private lateinit var commonSingletonComponentProvider: FeatureProvider<CommonSingletonComponent>
    private lateinit var employeesControllerWrapperProvider: FeatureProvider<EmployeesControllerWrapper.Provider>
    private lateinit var currentAccount: FeatureProvider<CurrentAccount>

    internal val singletonComponent by lazy {
        EmployeeSelectionSingletonComponentInitializer(
            object : EmployeeSelectionDependency,
                CurrentAccount by currentAccount.get(),
                EmployeesControllerWrapper.Provider by employeesControllerWrapperProvider.get() {}
        ).init(commonSingletonComponentProvider.get())
    }
    private val employeeSelectionFeature by lazy {
        EmployeeSelectionFeatureImpl(application)
    }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(EmployeeSelectionFeature::class.java) { employeeSelectionFeature },
        FeatureWrapper(EmployeesSelectionProvider::class.java) { employeeSelectionFeature },
        FeatureWrapper(EmployeesSelectionResultManagerProvider::class.java) { employeeSelectionFeature },
        FeatureWrapper(MutableEmployeesSelectionResultManagerProvider::class.java) { employeeSelectionFeature }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(CommonSingletonComponent::class.java) { commonSingletonComponentProvider = it }
        .require(EmployeesControllerWrapper.Provider::class.java) { employeesControllerWrapperProvider = it }
        .require(CurrentAccount::class.java) { currentAccount = it }
        .build()

    override val customizationOptions: Unit = Unit
}