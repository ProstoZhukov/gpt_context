package ru.tensor.sbis.business_card_list.presentation.router

import ru.tensor.sbis.business_card_list.contract.BusinessCardListDependency
import ru.tensor.sbis.business_card_list.contract.internal.list.BusinessCardListRouter
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCardLink
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareLink
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareParams
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableFragment
import ru.tensor.sbis.mvi_extension.router.fragment.FragmentRouter
import ru.tensor.sbis.mvi_extension.router.fragment.addFragmentWithBackStack

/** Реализация BusinessCardListRouter */
internal class BusinessCardListRouterImpl(
    private val dependency: BusinessCardListDependency,
    private val containerId: Int
) : BusinessCardListRouter, FragmentRouter() {

    override fun back() {
        execute {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun toLinkShare(links: ArrayList<BusinessCardLink>) {
        execute {
            val contentCreator = dependency.getLinkShareContentCreator(
                SbisLinkShareParams(
                    links = mapToSbisLinkShareLinks(links)
                )
            )

            val containerFragment = ContainerMovableFragment.Builder()
                .setContentCreator(contentCreator)
                .setExpandedPeekHeight(MovablePanelPeekHeight.FitToContent())
                .instant(true)
                .build()

            parentFragmentManager.addFragmentWithBackStack(containerFragment, containerId)
        }
    }

    private fun mapToSbisLinkShareLinks(links: ArrayList<BusinessCardLink>): List<SbisLinkShareLink> {
        return links.map { link ->
            SbisLinkShareLink(url = link.url, caption = link.title ?: "")
        }
    }

    override fun toBusinessCardItem(data: BusinessCard) {
        execute {
            val fragment = dependency.getBusinessCardFragment(data)
            parentFragmentManager.addFragmentWithBackStack(fragment, containerId)
        }
    }

    override fun showPinError(errorMessage: String) {
        SbisPopupNotification.push(SbisPopupNotificationStyle.ERROR, errorMessage)
    }
}