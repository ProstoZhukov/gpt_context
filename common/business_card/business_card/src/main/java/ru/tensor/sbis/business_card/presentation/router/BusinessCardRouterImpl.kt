package ru.tensor.sbis.business_card.presentation.router

import ru.tensor.sbis.android_ext_decl.AndroidComponent
import ru.tensor.sbis.business_card.contract.BusinessCardDependency
import ru.tensor.sbis.business_card.contract.internal.BusinessCardRouter
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCardLink
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareLink
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareParams
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableFragment
import ru.tensor.sbis.mvi_extension.router.fragment.FragmentRouter
import ru.tensor.sbis.mvi_extension.router.fragment.addFragmentWithBackStack

/** Реализация BusinessCardRouter */
internal class BusinessCardRouterImpl(
    private val androidComponent: AndroidComponent,
    private val dependency: BusinessCardDependency,
    private val containerId: Int
) : BusinessCardRouter, FragmentRouter() {

    override fun back() {
        androidComponent.getSupportFragmentManager().popBackStack()
    }

    override fun toLinkShare(links: ArrayList<BusinessCardLink>) {
        val contentCreator = dependency.getLinkShareContentCreator(
            SbisLinkShareParams(
                links = mapToSbisLinkShareLinks(links)
            )
        )
        showContainerMovableFragment(contentCreator)
    }

    private fun mapToSbisLinkShareLinks(links: ArrayList<BusinessCardLink>): List<SbisLinkShareLink> {
        return links.map { link ->
            SbisLinkShareLink(url = link.url, caption = link.title ?: "")
        }
    }

    private fun showContainerMovableFragment(creator: ContentCreatorParcelable) {
        execute {
            val containerFragment = ContainerMovableFragment.Builder()
                .setContentCreator(creator)
                .setExpandedPeekHeight(MovablePanelPeekHeight.FitToContent())
                .instant(true)
                .build()

            parentFragmentManager.addFragmentWithBackStack(containerFragment, containerId)
        }
    }

}