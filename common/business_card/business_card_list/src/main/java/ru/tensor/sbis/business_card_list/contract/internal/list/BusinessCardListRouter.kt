package ru.tensor.sbis.business_card_list.contract.internal.list

import androidx.fragment.app.Fragment
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCardLink
import ru.tensor.sbis.mvi_extension.router.Router

/**@SelfDocumented*/
internal interface BusinessCardListRouter : Router<Fragment> {

    /**@SelfDocumented*/
    fun back()

    /**@SelfDocumented*/
    fun toLinkShare(links: ArrayList<BusinessCardLink>)

    /**@SelfDocumented*/
    fun toBusinessCardItem(data: BusinessCard)

    /**@SelfDocumented*/
    fun showPinError(errorMessage: String)
}