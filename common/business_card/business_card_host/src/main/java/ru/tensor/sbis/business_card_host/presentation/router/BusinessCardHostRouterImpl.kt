package ru.tensor.sbis.business_card_host.presentation.router

import ru.tensor.sbis.business_card_host.contract.BusinessCardHostDependency
import ru.tensor.sbis.business_card_host.contract.internal.list.BusinessCardHostRouter
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard
import ru.tensor.sbis.mvi_extension.router.fragment.FragmentRouter
import ru.tensor.sbis.mvi_extension.router.fragment.addFragmentWithBackStack
import java.util.UUID

/** Реализация BusinessCardHostRouter */
internal class BusinessCardHostRouterImpl(
    private val dependency: BusinessCardHostDependency,
    private val containerId: Int
) : BusinessCardHostRouter, FragmentRouter() {

    override fun showBusinessCardFragment(data: BusinessCard) {
        execute {
            val fragment = dependency.getBusinessCardFragment(data.copy(title = ""))
            parentFragmentManager.addFragmentWithBackStack(fragment, containerId)
        }
    }

    override fun showBusinessCardListFragment(personUuid: UUID) {
        execute {
            val fragment = dependency.getBusinessCardListFragment(personUuid)
            parentFragmentManager.addFragmentWithBackStack(fragment, containerId)
        }
    }
}