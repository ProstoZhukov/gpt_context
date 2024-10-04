package ru.tensor.sbis.design.link_share.contract

import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.link_share.presentation.view.LinkShareFragment
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import ru.tensor.sbis.link_share.ui.LinkShareFragmentProvider
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareParams

/**@SelfDocumented*/
internal class LinkShareFeature : LinkShareFragmentProvider {

    override fun getLinkShareContentCreator(params: SbisLinkShareParams): ContentCreatorParcelable = Creator(params)
}

/**@SelfDocumented*/
@Parcelize
private class Creator(val params: SbisLinkShareParams) : ContentCreatorParcelable {

    override fun createFragment(): Fragment = LinkShareFragment.newInstance(params)
}