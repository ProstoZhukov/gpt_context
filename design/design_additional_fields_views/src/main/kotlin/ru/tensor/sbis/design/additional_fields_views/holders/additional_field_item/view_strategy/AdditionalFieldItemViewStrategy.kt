package ru.tensor.sbis.design.additional_fields_views.holders.additional_field_item.view_strategy

import android.view.View
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.additional_fields_views.models.AdditionalFieldsViewParams
import ru.tensor.sbis.design.utils.extentions.getColorDrawableFrom
import ru.tensor.sbis.design.utils.extentions.setBottomPadding
import ru.tensor.sbis.design.utils.extentions.setTopPadding
import ru.tensor.sbis.design.utils.extentions.setVerticalPadding
import ru.tensor.sbis.list.view.binding.DataBindingViewHolder

/**
 * Стратегия установки параметров отображения в холдере доп. поля.
 *
 * @author au.aleksikov
 */
interface AdditionalFieldItemViewStrategy {

    /**
     * Установка параметров отображения в зависимости от расположения холдера относительно списка.
     */
    fun applyBackgroundAndPadding(view: View, params: AdditionalFieldsViewParams, bindingHolder: DataBindingViewHolder)
}

/**
 * Стратегия установки параметров отображения холдера доп. поля, если оно сверху.
 *
 * @author au.aleksikov
 */
class TopItemViewStrategy : AbstractAdditionalFieldItemViewStrategy() {
    override val top: Boolean = true
    override val bottom: Boolean = false

    override fun applyViewParams(view: View, params: AdditionalFieldsViewParams, bindingHolder: DataBindingViewHolder) {
        view.setTopPadding(params.verticalPadding)
        super.applyViewParams(view, params, bindingHolder)
    }
}

/**
 * Стратегия установки параметров отображения холдера доп. поля, если оно снизу.
 *
 * @author au.aleksikov
 */
class BottomItemViewStrategy : AbstractAdditionalFieldItemViewStrategy() {
    override val top: Boolean = false
    override val bottom: Boolean = true

    override fun applyViewParams(view: View, params: AdditionalFieldsViewParams, bindingHolder: DataBindingViewHolder) {
        view.setBottomPadding(params.verticalPadding)
        super.applyViewParams(view, params, bindingHolder)
    }
}

/**
 * Стратегия установки параметров отображения холдера доп. поля, если оно в центре.
 *
 * @author au.aleksikov
 */
class CenterItemViewStrategy : AbstractAdditionalFieldItemViewStrategy() {
    override val top: Boolean = false
    override val bottom: Boolean = false

    override fun applyBackgroundAndPadding(
        view: View,
        params: AdditionalFieldsViewParams,
        bindingHolder: DataBindingViewHolder
    ) {
        view.getColorDrawableFrom(R.color.palette_color_white1)
    }
}

/**
 * Стратегия установки параметров отображения холдера доп. поля, если оно единственное.
 *
 * @author au.aleksikov
 */
class OnceItemViewStrategy : AbstractAdditionalFieldItemViewStrategy() {
    override val top: Boolean = true
    override val bottom: Boolean = true

    override fun applyViewParams(view: View, params: AdditionalFieldsViewParams, bindingHolder: DataBindingViewHolder) {
        view.setVerticalPadding(params.verticalPadding)
        super.applyViewParams(view, params, bindingHolder)
    }
}