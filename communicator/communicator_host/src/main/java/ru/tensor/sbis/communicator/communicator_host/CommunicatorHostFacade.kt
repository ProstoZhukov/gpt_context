package ru.tensor.sbis.communicator.communicator_host

import ru.tensor.sbis.communicator.common.data.model.CommunicatorHostFragmentFactory
import ru.tensor.sbis.communicator.communicator_host.contract.CommunicatorHostDependency
import ru.tensor.sbis.communicator.communicator_host.contract.CommunicatorHostFeature
import ru.tensor.sbis.communicator.communicator_host.hostfragment.CommunicatorHostFragment

/**
 * Фасад модуля communicator_host.
 * Предоставляет фичи [CommunicatorHostFeature] и зависимости [CommunicatorHostDependency] модуля.
 *
 * @author da.zhukov
 */
internal object CommunicatorHostFacade : CommunicatorHostFeature,
    CommunicatorHostDependency.Provider,
    CommunicatorHostFragmentFactory by CommunicatorHostFragment.Companion {

    override lateinit var communicatorHostDependency: CommunicatorHostDependency

    fun configure(dependency: CommunicatorHostDependency) {
        communicatorHostDependency = dependency
    }
}