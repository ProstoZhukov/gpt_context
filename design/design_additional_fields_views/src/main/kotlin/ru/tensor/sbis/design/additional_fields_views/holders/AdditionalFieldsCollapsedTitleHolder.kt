package ru.tensor.sbis.design.additional_fields_views.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.design.additional_fields_views.databinding.DesignAdditionalFieldsCollapsedTitleBinding
import ru.tensor.sbis.design.R as RDesign

/**
 * Холдер для отображения заголовка дополнительно.
 *
 * @author au.aleksikov
 */
class AdditionalFieldsCollapsedTitleHolder(
    parent: ViewGroup,
    private val onTitleClicks: PublishSubject<Boolean>,
    binding: DesignAdditionalFieldsCollapsedTitleBinding = DesignAdditionalFieldsCollapsedTitleBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
    )
) : AbstractViewHolder<BaseItem<Any>>(binding.root) {

    private val icon = binding.designAdditionalFieldsExpandedTitleArrow

    override fun bind(dataModel: BaseItem<Any>) {
        super.bind(dataModel)

        val collapse = dataModel.data as Boolean
        val arrowDown = itemView.resources.getString(RDesign.string.design_mobile_icon_arrow)
        val arrowUp = itemView.resources.getString(RDesign.string.design_mobile_icon_arrow_small_up)

        icon.text = if (collapse) arrowUp else arrowDown

        itemView.setOnClickListener {
            onTitleClicks.onNext(collapse.not())
        }
    }
}