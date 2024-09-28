package ru.tensor.sbis.communicator.communicator_host.contract

import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorHostRouter

/**
 * Внешние зависимости модуля communicator_host
 * @see CommunicatorHostRouter.Provider
 *
 * @author da.zhukov
 */
interface CommunicatorHostDependency : CommunicatorHostRouter.Provider {

    interface Provider {

        val communicatorHostDependency: CommunicatorHostDependency
    }
}