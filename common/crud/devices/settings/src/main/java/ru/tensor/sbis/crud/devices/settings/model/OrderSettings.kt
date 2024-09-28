package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.retail_settings.generated.OrderSettings as ControllerOrderSettings

/** @SelfDocumented. */
@Parcelize
data class OrderSettings(
    val fontBarcodes: Int,
    val fontByNoms: Int,
    val fontCourses: Int,
    val fontKitchenOrders: Int,
    val fontTableNumber: Int,
    val fontWaiterName: Int,
    val frame: String?,
    val hasOwn: Boolean,
    val leftMargin: Int,
    val printBarcodes: Boolean,
    val printCourses: Boolean,
    val printMsg: Boolean,
    val printTableNumber: Boolean,
    val printWaiterName: Boolean,
    val rightMargin: Int,
    val showBigNumber: Boolean,
    val splitByNoms: Boolean,
    val splitByProduction: Boolean,
    val topOffset: Int,
) : Parcelable

/** @SelfDocumented. */
fun ControllerOrderSettings.toAndroid() = OrderSettings(
    fontBarcodes = fontBarcodes,
    fontByNoms = fontByNoms,
    fontCourses = fontCourses,
    fontKitchenOrders = fontKitchenOrders,
    fontTableNumber = fontTableNumber,
    fontWaiterName = fontWaiterName,
    frame = frame,
    hasOwn = hasOwn,
    leftMargin = leftMargin,
    printBarcodes = printBarcodes,
    printCourses = printCourses,
    printMsg = printMsg,
    printTableNumber = printTableNumber,
    printWaiterName = printWaiterName,
    rightMargin = rightMargin,
    showBigNumber = showBigNumber,
    splitByNoms = splitByNoms,
    splitByProduction = splitByProduction,
    topOffset = topOffset,
)

/** @SelfDocumented. */
fun OrderSettings.toController() = ControllerOrderSettings(
    fontBarcodes = fontBarcodes,
    fontByNoms = fontByNoms,
    fontCourses = fontCourses,
    fontKitchenOrders = fontKitchenOrders,
    fontTableNumber = fontTableNumber,
    fontWaiterName = fontWaiterName,
    frame = frame,
    hasOwn = hasOwn,
    leftMargin = leftMargin,
    printBarcodes = printBarcodes,
    printCourses = printCourses,
    printMsg = printMsg,
    printTableNumber = printTableNumber,
    printWaiterName = printWaiterName,
    rightMargin = rightMargin,
    showBigNumber = showBigNumber,
    splitByNoms = splitByNoms,
    splitByProduction = splitByProduction,
    topOffset = topOffset,
)