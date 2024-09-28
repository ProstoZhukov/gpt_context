package ru.tensor.sbis.business_card_host.contract.internal.list

import androidx.fragment.app.Fragment
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard
import ru.tensor.sbis.mvi_extension.router.Router
import java.util.UUID

/**@SelfDocumented*/
internal interface BusinessCardHostRouter : Router<Fragment> {

    /**@SelfDocumented*/
    fun showBusinessCardFragment(data: BusinessCard)

    /**@SelfDocumented*/
    fun showBusinessCardListFragment(personUuid: UUID)
}