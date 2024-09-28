package ru.tensor.sbis.communicator.communicator_navigation

import ru.tensor.sbis.communicator.communicator_navigation.contract.CommunicatorNavigationDependency
import ru.tensor.sbis.communicator.communicator_navigation.contract.CommunicatorNavigationFeature
import ru.tensor.sbis.communicator.communicator_navigation.navigation.CommunicatorRouterImpl

/**
 * Фасад модуля навигации коммуникатора.
 * Предоставляет фичи [CommunicatorNavigationFeature] и зависимости [CommunicatorNavigationDependency] модуля.
 *
 * @author da.zhukov
 */
internal object CommunicatorNavigationFacade :
    CommunicatorNavigationDependency.Provider,
    CommunicatorNavigationFeature by CommunicatorRouterImpl.Companion {

    override lateinit var communicatorNavigationDependency: CommunicatorNavigationDependency

    fun configure(dependency: CommunicatorNavigationDependency) {
        communicatorNavigationDependency = dependency
    }
}