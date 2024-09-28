package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.retail_settings.generated.PrintingSettings as ControllerPrintingSettings

/** @SelfDocumented. */
@Parcelize
data class PrintingSettings(
    val bill: BillSettings,
    val order: OrderSettings,
    val receipt: ReceiptSettings
) : Parcelable {

    /** @SelfDocumented. */
    companion object Mocks {

        /** @SelfDocumented. */
        fun getMock() = PrintingSettings(
            bill = BillSettings(
                fontMsg = 0,
                fontTableNumber = 0,
                fontWaiterName = 0,
                frame = "",
                hasOwn = 0,
                leftMargin = 0,
                printMsg = "",
                printSaleLink = 0,
                printTableNumber = 0,
                printWaiterName = 0,
                rightMargin = 0,
                saleLinkComment = "",
                saleLinkFont = 0,
                showBonusInfo = 0,
                showCaloriesInfo = 0,
                showCustomerInfo = 0,
                topOffset = 0,
            ),
            order = OrderSettings(
                fontBarcodes = 0,
                fontByNoms = 0,
                fontCourses = 0,
                fontKitchenOrders = 0,
                fontTableNumber = 0,
                fontWaiterName = 0,
                frame = "",
                hasOwn = false,
                leftMargin = 0,
                printBarcodes = false,
                printCourses = false,
                printMsg = false,
                printTableNumber = false,
                printWaiterName = false,
                rightMargin = 0,
                showBigNumber = false,
                splitByNoms = false,
                splitByProduction = false,
                topOffset = 0,
            ),
            receipt = ReceiptSettings(
                bottomMessage = "",
                frame = "",
                hasSaleComment = false,
                hasSaleNumber = false,
                isPersonalSettings = false,
                printSaleLink = false,
                saleLinkComment = "",
                saleLinkFont = 0,
                showBonusInfo = false,
                showCaloriesInfo = false,
                showCardInfo = false,
                showCustomerInfo = false,
                showDiscount = false,
                showGoodComment = false,
                showPromoCode = false,
                showQuestionaryUrl = false,
                showSaleNumber = false,
                topMessage = "",
            )
        )
    }
}

/** @SelfDocumented. */
fun ControllerPrintingSettings.toAndroid() = PrintingSettings(
    bill = bill.toAndroid(),
    order = order.toAndroid(),
    receipt = receipt.toAndroid(),
)

/** @SelfDocumented. */
fun PrintingSettings.toController() = ControllerPrintingSettings(
    bill = bill.toController(),
    order = order.toController(),
    receipt = receipt.toController(),
)