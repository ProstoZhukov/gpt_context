package ru.tensor.sbis.design.link_share.utils

import ru.tensor.sbis.link_share.ui.model.SbisLinkShareParams

/** Реализация [LinkShareURLProvider] */
internal class LinkShareParamsProviderImpl(private val params: SbisLinkShareParams) : LinkShareURLProvider {
    override fun getURLForSelectedTab(selectedLinkTab: Int): String {
        return params.links.getOrNull(selectedLinkTab)?.url ?: ""
    }
}