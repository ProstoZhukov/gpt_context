package ru.tensor.sbis.complain_service

import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.communication_decl.complain.ComplainService
import ru.tensor.sbis.communication_decl.complain.data.ComplainUseCase
import ru.tensor.sbis.complain_service.contract.ComplainServiceDependency
import ru.tensor.sbis.complain_service.contract.ComplainServiceFeature
import ru.tensor.sbis.complain_service.domain.ComplainDialogFragment
import ru.tensor.sbis.complain_service.domain.ComplainServiceImpl

/**
 * Реализация [ComplainServiceFeature].
 *
 * @author da.zhukov
 */
internal object ComplainServiceFeatureFacade : ComplainServiceFeature {

    private lateinit var dependency: ComplainServiceDependency

    private lateinit var complainService: ComplainService

    override fun getComplainService(): ComplainService =
        complainService

    override fun showComplainDialogFragment(fragmentManager: FragmentManager, useCase: ComplainUseCase) {
        ComplainDialogFragment.showComplainDialogFragment(fragmentManager, useCase)
    }

    fun configure(dependency: ComplainServiceDependency) {
        this.dependency = dependency
        complainService = ComplainServiceImpl(dependency.loginInterface)
    }
}