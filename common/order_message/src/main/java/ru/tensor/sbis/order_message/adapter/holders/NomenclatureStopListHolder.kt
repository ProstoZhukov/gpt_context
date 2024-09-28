package ru.tensor.sbis.order_message.adapter.holders

import android.view.ViewGroup
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.common.util.formatDuration
import ru.tensor.sbis.common_catalog_items_design.catalog_item.CatalogItemModel
import ru.tensor.sbis.common_catalog_items_design.catalog_item.simple_catalog_item.view.SimpleCatalogItemView
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.utils.extentions.setLeftPadding
import ru.tensor.sbis.order_message.Nomenclature

/**
 * Холдер номенклатуры в стоп листе
 */
internal class StopListNomenclatureHolder(parent: ViewGroup, private val withImage: Boolean) :
    AbstractViewHolder<BaseItem<Any>>(SimpleCatalogItemView(parent.context)) {

    override fun bind(dataModel: BaseItem<Any>) {
        super.bind(dataModel)
        with(dataModel.data as Nomenclature) {
            val subtitle = if (duration > 0) duration.formatDuration() else packName

            val catalogItem =
                CatalogItemModel.SimpleItem(
                    title = name,
                    subtitle = subtitle,
                    withImage = withImage,
                    imageUrl = imageUrl,
                    count = availableQuantity.toString()
                )

            itemView.setLeftPadding(itemView.resources.dp(12))

            (itemView as SimpleCatalogItemView).data = catalogItem
        }
    }
}