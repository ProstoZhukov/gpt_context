package ru.tensor.sbis.business_card_list.presentation.holder

import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import ru.tensor.sbis.business_card_list.presentation.view.ClicksWrapper
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**@SelfDocumented*/
internal class BusinessCardHolderHelper(
    private val clicksWrapper: ClicksWrapper,
    private val scope: LifecycleCoroutineScope,
) : ViewHolderHelper<BusinessCard, BusinessCardHolder> {

    override fun createViewHolder(parentView: ViewGroup): BusinessCardHolder =
        BusinessCardHolder(parentView, clicksWrapper, scope)

    override fun bindToViewHolder(data: BusinessCard, viewHolder: BusinessCardHolder) = viewHolder.bind(data)
}