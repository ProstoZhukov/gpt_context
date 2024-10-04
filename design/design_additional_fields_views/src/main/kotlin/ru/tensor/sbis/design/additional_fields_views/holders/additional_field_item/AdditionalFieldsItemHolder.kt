package ru.tensor.sbis.design.additional_fields_views.holders.additional_field_item

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.design.additional_fields_views.databinding.DesignAdditionalFieldsItemBinding
import ru.tensor.sbis.design.additional_fields_views.holders.additional_field_item.view_strategy.BottomItemViewStrategy
import ru.tensor.sbis.design.additional_fields_views.holders.additional_field_item.view_strategy.CenterItemViewStrategy
import ru.tensor.sbis.design.additional_fields_views.holders.additional_field_item.view_strategy.OnceItemViewStrategy
import ru.tensor.sbis.design.additional_fields_views.holders.additional_field_item.view_strategy.TopItemViewStrategy
import ru.tensor.sbis.design.additional_fields_views.models.AdditionalFieldsBackgroundType
import ru.tensor.sbis.design.additional_fields_views.models.AdditionalFieldsViewParams
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.utils.extentions.setHorizontalMargin
import ru.tensor.sbis.design.utils.extentions.setHorizontalPadding
import ru.tensor.sbis.list.view.binding.BindingItem
import ru.tensor.sbis.list.view.binding.DataBindingViewHolder

/**
 * Холдер для отображения элемента дополнительно.
 *
 * @author au.aleksikov
 */
class AdditionalFieldsItemHolder private constructor(
    binding: DesignAdditionalFieldsItemBinding,
    val params: AdditionalFieldsViewParams
) :
    AbstractViewHolder<BaseItem<Any>>(binding.root) {

    private val container: FrameLayout = binding.designAdditionalFieldsItemContainer

    constructor(parent: ViewGroup, params: AdditionalFieldsViewParams) : this(
        DesignAdditionalFieldsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        params
    )

    private lateinit var bindingHolder: DataBindingViewHolder

    @Suppress("UNCHECKED_CAST")
    override fun bind(model: BaseItem<Any>) {
        super.bind(model)
        val item = model.data as Pair<BindingItem<Any>, AdditionalFieldsBackgroundType>

        if (::bindingHolder.isInitialized) {
            bindingHolder.onDetach()
            bindingHolder.destroy()
        }

        bindingHolder = createAndBindViewHolder(item)
        bindingHolder.onAttach()

        manageBackgroundAndPadding(item)
        updateViewElements()
    }

    private fun createAndBindViewHolder(item: Pair<BindingItem<Any>, AdditionalFieldsBackgroundType>): DataBindingViewHolder {
        val viewHolder = item.first.createViewHolder(container)
        item.first.bindToViewHolder(item.first.data, viewHolder)
        return viewHolder
    }

    private fun manageBackgroundAndPadding(item: Pair<BindingItem<Any>, AdditionalFieldsBackgroundType>) {
        val strategiesMap = mapOf(
            AdditionalFieldsBackgroundType.FIRST to TopItemViewStrategy(),
            AdditionalFieldsBackgroundType.LAST to BottomItemViewStrategy(),
            AdditionalFieldsBackgroundType.CENTER to CenterItemViewStrategy(),
            AdditionalFieldsBackgroundType.ONCE to OnceItemViewStrategy()
        )

        val horizontalMargin = itemView.dp(params.horizontalMargin)
        val strategy = strategiesMap[item.second] ?: CenterItemViewStrategy()
        strategy.applyBackgroundAndPadding(itemView, params, bindingHolder)

        bindingHolder.itemView.setHorizontalPadding(itemView.dp(4))
        container.setHorizontalMargin(horizontalMargin, horizontalMargin)
    }

    private fun updateViewElements() {
        (itemView as FrameLayout).apply {
            removeAllViews()
            addView(bindingHolder.itemView)
        }
    }
}