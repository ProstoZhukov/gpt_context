package ru.tensor.sbis.business_card.contract.internal

import androidx.fragment.app.Fragment
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCardLink
import ru.tensor.sbis.mvi_extension.router.Router

/**@SelfDocumented*/
internal interface BusinessCardRouter : Router<Fragment>  {

    /**@SelfDocumented*/
    fun back()

    /**@SelfDocumented*/
    fun toLinkShare(links: ArrayList<BusinessCardLink>)
}