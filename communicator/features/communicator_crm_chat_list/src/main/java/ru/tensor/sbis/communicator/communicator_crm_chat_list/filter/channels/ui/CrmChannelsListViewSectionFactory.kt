package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import ru.tensor.sbis.common.util.setActionOnClick
import ru.tensor.sbis.communicator.communicator_crm_chat_list.R
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.models.SelectableFilterItem
import ru.tensor.sbis.communicator.declaration.crm.model.CRMRadioButtonFilterType
import ru.tensor.sbis.crud3.ItemWithSection
import ru.tensor.sbis.crud3.view.FirstItemFactory
import ru.tensor.sbis.crud3.view.FirstItemHiddenWhenEmpty
import ru.tensor.sbis.crud3.view.Refreshable
import ru.tensor.sbis.list.view.binding.DataBindingViewHolder
import ru.tensor.sbis.list.view.binding.DataBindingViewHolderHelper
import ru.tensor.sbis.list.view.binding.LayoutIdViewFactory
import ru.tensor.sbis.list.view.binding.ViewFactory
import ru.tensor.sbis.list.view.item.Item
import ru.tensor.sbis.list.view.item.ItemOptions
import ru.tensor.sbis.list.view.section.Options
import ru.tensor.sbis.list.view.item.Options as ItemOption
import ru.tensor.sbis.design.R as RDesign


/** @SelfDocumented */
internal class CrmChannelsListViewSectionFactory(
    private val clickDelegate: CrmChannelsListSectionClickDelegate,
    private val firstItemHolderHelper: FirstItemHolderHelper
) : FirstItemFactory<CrmChannelsListSectionItem> {

    override fun create(): ItemWithSection<CrmChannelsListSectionItem> {
        val type = CRMRadioButtonFilterType.OPERATOR_FILTER_NOT_CHOSEN
        return ItemWithSection(
            sectionOption = Options(
                hasDividers = false,
                hasTopMargin = false
            ),
            item = CrmChannelsListSectionItem(
                selectableFilterItem = SelectableFilterItem(
                    type = type,
                    titleRes = type.textRes,
                    isSelected = true,
                    clickAction = { clickDelegate.onSectionClick() }
                ),
                options = ItemOption(
                    customBackground = true,
                    customSidePadding = true
                ),
                firstItemHolderHelper = firstItemHolderHelper
            )
        )
    }
}

internal class CrmChannelsListSectionItemHolderHelper(
    private val factory: ViewFactory = LayoutIdViewFactory(R.layout.communicator_crm_chat_selectable_filter_item),
    private val firstItemHolderHelper: FirstItemHolderHelper
) : DataBindingViewHolderHelper<SelectableFilterItem>(factory) {
    private var firstItem: View? = null

    override fun createViewHolder(parentView: ViewGroup): DataBindingViewHolder {
        firstItem = factory.createView(parentView)
        return super.createViewHolder(parentView)
    }

    override fun bindToViewHolder(data: SelectableFilterItem, viewHolder: DataBindingViewHolder) {
        val itemView = viewHolder.itemView
        if (itemView != firstItem) {
            itemView.isClickable = true
            firstItemHolderHelper.attachFirstItem(itemView)
            firstItem = itemView
        }
        super.bindToViewHolder(data, viewHolder)
    }
}

/** @SelfDocumented */
internal class CrmChannelsListSectionItem(
    selectableFilterItem: SelectableFilterItem,
    options: ItemOptions = ItemOption(),
    firstItemHolderHelper: FirstItemHolderHelper
) : Refreshable, FirstItemHiddenWhenEmpty, Item<SelectableFilterItem, DataBindingViewHolder>(
    data = selectableFilterItem,
    viewHolderHelper = CrmChannelsListSectionItemHolderHelper(firstItemHolderHelper = firstItemHolderHelper),
    options = options
)

internal interface CrmChannelsListSectionClickDelegate {
    fun onSectionClick()
}

internal class FirstItemHolderHelper(context: Context) {
    private var firstItem: View? = null
    private val firstItemShowedMinHeight by lazy {
        context.resources.getDimensionPixelSize(RDesign.dimen.selection_window_item_height)
    }

    fun attachFirstItem(view: View) {
        firstItem = view
    }

    /** @SelfDocumented */
    fun hideFirstItem() {
        if (firstItem?.isVisible == false) return
        firstItem?.minimumHeight = 0
        firstItem?.isVisible = false
        if (firstItem is ViewGroup) {
            (firstItem as ViewGroup).children.forEach { it.isVisible = false }
        }
    }

    /** @SelfDocumented */
    fun showFirstItem() {
        if (firstItem?.isVisible == true) return
        firstItem?.minimumHeight = firstItemShowedMinHeight
        firstItem?.isVisible = true
        if (firstItem is ViewGroup) {
            (firstItem as ViewGroup).children.forEach { it.isVisible = true }
        }
    }
}