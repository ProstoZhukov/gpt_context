package ru.tensor.sbis.design.additional_fields_views.holders.additional_field_item.view_strategy

import android.view.View
import ru.tensor.sbis.design.additional_fields_views.models.AdditionalFieldsViewParams
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.view_ext.round_corner.setRoundedSideOutlineProvider
import ru.tensor.sbis.list.view.binding.DataBindingViewHolder

/**
 * Реализация [AdditionalFieldItemViewStrategy] для установки параметров отображения доп. поля.
 *
 * @author au.aleksikov
 */
abstract class AbstractAdditionalFieldItemViewStrategy : AdditionalFieldItemViewStrategy {

    /** Является ли верхним элементом*/
    abstract val top: Boolean

    /** Является ли нижним элементом*/
    abstract val bottom: Boolean

    override fun applyBackgroundAndPadding(
        view: View,
        params: AdditionalFieldsViewParams,
        bindingHolder: DataBindingViewHolder
    ) {
        applyViewParams(view, params, bindingHolder)
    }

    /** Применить параметры отображения */
    open fun applyViewParams(
        view: View,
        params: AdditionalFieldsViewParams,
        bindingHolder: DataBindingViewHolder
    ) {
        bindingHolder.itemView.setRoundedSideOutlineProvider(
            view.dp(params.backgroundRadius),
            top = top,
            bottom = bottom
        )
    }
}