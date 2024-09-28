package ru.tensor.sbis.business_card_list.domain.command

import androidx.lifecycle.LifecycleCoroutineScope
import ru.tensor.sbis.business_card_list.presentation.holder.BusinessCardHolderHelper
import ru.tensor.sbis.business_card_list.presentation.view.ClicksWrapper
import ru.tensor.sbis.business_card_host_decl.ui.model.BusinessCard
import ru.tensor.sbis.crud3.domain.ItemInSectionMapper
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.item.Item
import ru.tensor.sbis.list.view.item.Options
import ru.tensor.sbis.list.view.item.comparator.ComparableItem
import ru.tensor.sbis.list.view.section.SectionOptions
import java.util.UUID
import ru.tensor.sbis.list.view.section.Options as SectionOptionsImpl

/**@SelfDocumented*/
internal class BusinessCardListMapper(
    private val clicksWrapper: ClicksWrapper,
    private val scope: LifecycleCoroutineScope,
) : ItemInSectionMapper<BusinessCard, AnyItem> {

    private val options: Options by lazy {
        Options(useCustomListeners = true, customSidePadding = true, customBackground = true)
    }

    private val sectionOptions by lazy { SectionOptionsImpl(hasDividers = false) }

    override fun map(item: BusinessCard, defaultClickAction: (BusinessCard) -> Unit): AnyItem =
        Item(
            item,
            BusinessCardHolderHelper(clicksWrapper, scope),
            ComparableBusinessCard(item.id, item.style?.properties?.background ?: ""),
            options
        )

    override fun mapSection(item: BusinessCard): SectionOptions = sectionOptions
}

private class ComparableBusinessCard(private val id: UUID, private val background: String) :
    ComparableItem<BusinessCard> {

    override fun areTheSame(otherItem: BusinessCard): Boolean =
        id == otherItem.id && background == otherItem.style?.properties?.background
}