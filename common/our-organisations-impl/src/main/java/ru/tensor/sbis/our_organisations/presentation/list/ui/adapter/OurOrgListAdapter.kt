package ru.tensor.sbis.our_organisations.presentation.list.ui.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.base_components.adapter.universal.ItemClickHandler
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingAdapter
import ru.tensor.sbis.base_components.adapter.universal.UniversalBindingItem
import ru.tensor.sbis.base_components.adapter.universal.UniversalTwoWayPaginationAdapter
import ru.tensor.sbis.base_components.adapter.universal.UniversalViewHolder
import ru.tensor.sbis.our_organisations.BR
import ru.tensor.sbis.our_organisations.R
import ru.tensor.sbis.our_organisations.data.OurOrgItemType

/**
 * Адаптер экрана нашей организации, для отображения организации, для типа [OurOrgItemType.COMPLEX] вместе с городом.
 *
 * @author mv.ilin
 */
internal class OurOrgListAdapter(
    private val type: OurOrgItemType,
    private val isMultipleChoice: Boolean,
    private val buttonAction: ItemClickHandler<OrganisationVM>,
    private val checkboxAction: ItemClickHandler<OrganisationVM>,
) : UniversalTwoWayPaginationAdapter<UniversalBindingItem>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractViewHolder<UniversalBindingItem> {
        return when (viewType) {
            TYPE_ORGANISATION_ITEM -> {
                val binding = UniversalBindingAdapter.createBinding(getLayoutId(), parent)
                binding.setVariable(BR.isMultipleChoice, isMultipleChoice)
                binding.setVariable(BR.checkboxClickHandler, checkboxAction)
                binding.setVariable(BR.clickHandler, buttonAction)
                UniversalViewHolder(binding)
            }

            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setContent(newContent: List<UniversalBindingItem>?) {
        super.setContent(newContent, false)
        notifyContentChanged()
        notifyDataSetChanged()
    }

    fun addNewPage(newContent: List<UniversalBindingItem>) {
        val count = newContent.size - itemCount + 1
        setContent(newContent, false)
        notifyItemRangeChanged(itemCount - count, count)
    }

    private fun getLayoutId() = when (type) {
        OurOrgItemType.SIMPLE -> R.layout.our_org_item_simple
        OurOrgItemType.COMPLEX -> R.layout.our_org_item_complex
    }
}
