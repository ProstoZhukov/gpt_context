package ru.tensor.sbis.order_message.adapter.holders

import android.view.ViewGroup
import ru.tensor.sbis.base_components.adapter.AbstractViewHolder
import ru.tensor.sbis.business.business_decl.receipt.model.PurchaseData
import ru.tensor.sbis.business.business_decl.receipt.model.ReceiptData.ImageType.CIRCLE
import ru.tensor.sbis.business.business_decl.receipt.model.ReceiptData.ImageType.NO_IMAGE
import ru.tensor.sbis.business.receipt.view.card.view_wrappers.ReceiptPurchaseView
import ru.tensor.sbis.common.data.model.base.BaseItem
import ru.tensor.sbis.order_message.Nomenclature
import ru.tensor.sbis.order_message.R

/**
 * Вью-холдер для отображения ячейки номенклатуры
 */
internal class NomenclatureHolder(parent: ViewGroup, private val withImage: Boolean) :
    AbstractViewHolder<BaseItem<Any>>(ReceiptPurchaseView(parent.context)
        .apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }) {

    /**@SelfDocumented*/
    override fun bind(dataModel: BaseItem<Any>) {
        super.bind(dataModel)
        val data = (dataModel.data as Nomenclature).mapToPurchaseData()
        (itemView as ReceiptPurchaseView).setModel(data, false)
    }

    private fun Nomenclature.mapToPurchaseData(): PurchaseData {
        return PurchaseData(
            positionId = "0",
            receiptId = "",
            nomenclatureUuid = uuid.toString(),
            nomenclatureCode = "",
            productName = name,
            productAmount = 1.0,
            productUnit = "",
            unitPrice = price,
            checkPrice = price,
            totalPrice = price,
            returnDate = "",
            manualDiscount = false,
            displayType = PurchaseData.DisplayType.SINGLE_ITEM,
            isValidForDetails = false,
            imageUrl = imageUrl,
            imageType = if (withImage) CIRCLE else NO_IMAGE,
            clickableType = PurchaseData.ClickableType.NOT_CLICKABLE,
            kopecksStyle = R.style.SabyClientsDesignReceiptKopeckPurchase
        )
    }
}