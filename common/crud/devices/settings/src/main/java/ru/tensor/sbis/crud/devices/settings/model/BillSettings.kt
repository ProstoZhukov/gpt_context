package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.retail_settings.generated.BillSettings as ControllerBillSettings

/** @SelfDocumented. */
@Parcelize
data class BillSettings(
    val fontMsg: Int,
    val fontTableNumber: Int,
    val fontWaiterName: Int,
    val frame: String?,
    val hasOwn: Int,
    val leftMargin: Int,
    val printMsg: String,
    val printSaleLink: Int,
    val printTableNumber: Int,
    val printWaiterName: Int,
    val rightMargin: Int,
    val saleLinkComment: String,
    val saleLinkFont: Int,
    val showBonusInfo: Int,
    val showCaloriesInfo: Int,
    val showCustomerInfo: Int,
    val topOffset: Int,
) : Parcelable

/** @SelfDocumented. */
fun ControllerBillSettings.toAndroid() = BillSettings(
    fontMsg = fontMsg,
    fontTableNumber = fontTableNumber,
    fontWaiterName = fontWaiterName,
    frame = frame,
    hasOwn = hasOwn,
    leftMargin = leftMargin,
    printMsg = printMsg,
    printSaleLink = printSaleLink,
    printTableNumber = printTableNumber,
    printWaiterName = printWaiterName,
    rightMargin = rightMargin,
    saleLinkComment = saleLinkComment,
    saleLinkFont = saleLinkFont,
    showBonusInfo = showBonusInfo,
    showCaloriesInfo = showCaloriesInfo,
    showCustomerInfo = showCustomerInfo,
    topOffset = topOffset,
)

/** @SelfDocumented. */
fun BillSettings.toController() = ControllerBillSettings(
    fontMsg = fontMsg,
    fontTableNumber = fontTableNumber,
    fontWaiterName = fontWaiterName,
    frame = frame,
    hasOwn = hasOwn,
    leftMargin = leftMargin,
    printMsg = printMsg,
    printSaleLink = printSaleLink,
    printTableNumber = printTableNumber,
    printWaiterName = printWaiterName,
    rightMargin = rightMargin,
    saleLinkComment = saleLinkComment,
    saleLinkFont = saleLinkFont,
    showBonusInfo = showBonusInfo,
    showCaloriesInfo = showCaloriesInfo,
    showCustomerInfo = showCustomerInfo,
    topOffset = topOffset,
)