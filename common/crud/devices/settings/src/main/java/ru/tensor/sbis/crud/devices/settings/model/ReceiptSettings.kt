package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.retail_settings.generated.ReceiptSettings as ControllerReceiptSettings

/** @SelfDocumented. */
@Parcelize
data class ReceiptSettings(
    val bottomMessage: String,
    val frame: String?,
    val hasSaleComment: Boolean,
    val hasSaleNumber: Boolean,
    val isPersonalSettings: Boolean,
    val printSaleLink: Boolean,
    val saleLinkComment: String,
    val saleLinkFont: Int,
    val showBonusInfo: Boolean,
    val showCaloriesInfo: Boolean,
    val showCardInfo: Boolean,
    val showCustomerInfo: Boolean,
    val showDiscount: Boolean,
    val showGoodComment: Boolean,
    val showPromoCode: Boolean,
    val showQuestionaryUrl: Boolean,
    val showSaleNumber: Boolean,
    val topMessage: String,
) : Parcelable

/** @SelfDocumented. */
fun ControllerReceiptSettings.toAndroid() = ReceiptSettings(
    bottomMessage = bottomMessage,
    frame = frame,
    hasSaleComment = hasSaleComment,
    hasSaleNumber = hasSaleNumber,
    isPersonalSettings = isPersonalSettings,
    printSaleLink = printSaleLink,
    saleLinkComment = saleLinkComment,
    saleLinkFont = saleLinkFont,
    showBonusInfo = showBonusInfo,
    showCaloriesInfo = showCaloriesInfo,
    showCardInfo = showCardInfo,
    showCustomerInfo = showCustomerInfo,
    showDiscount = showDiscount,
    showGoodComment = showGoodComment,
    showPromoCode = showPromoCode,
    showQuestionaryUrl = showQuestionaryUrl,
    showSaleNumber = showSaleNumber,
    topMessage = topMessage,
)

/** @SelfDocumented. */
fun ReceiptSettings.toController() = ControllerReceiptSettings(
    bottomMessage = bottomMessage,
    frame = frame,
    hasSaleComment = hasSaleComment,
    hasSaleNumber = hasSaleNumber,
    isPersonalSettings = isPersonalSettings,
    printSaleLink = printSaleLink,
    saleLinkComment = saleLinkComment,
    saleLinkFont = saleLinkFont,
    showBonusInfo = showBonusInfo,
    showCaloriesInfo = showCaloriesInfo,
    showCardInfo = showCardInfo,
    showCustomerInfo = showCustomerInfo,
    showDiscount = showDiscount,
    showGoodComment = showGoodComment,
    showPromoCode = showPromoCode,
    showQuestionaryUrl = showQuestionaryUrl,
    showSaleNumber = showSaleNumber,
    topMessage = topMessage,
)