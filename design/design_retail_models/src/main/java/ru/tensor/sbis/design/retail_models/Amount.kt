package ru.tensor.sbis.design.retail_models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import ru.tensor.sbis.design.retail_models.utils.ZERO_MONEY_VALUE
import ru.tensor.sbis.design.retail_models.utils.isMoreZero
import ru.tensor.sbis.design.retail_models.utils.isZero

/**
 * Модель "Суммы" (количество денег)
 */
@Parcelize
data class Amount(
    /**@SelfDocumented */
    var cash: BigDecimal = ZERO_MONEY_VALUE,
    /**@SelfDocumented */
    var card: BigDecimal = ZERO_MONEY_VALUE,
    /**@SelfDocumented */
    var underSalary: BigDecimal = ZERO_MONEY_VALUE,
    /**@SelfDocumented */
    var qrCode: BigDecimal = ZERO_MONEY_VALUE,
    /**@SelfDocumented */
    var internet: BigDecimal = ZERO_MONEY_VALUE
) : Parcelable {

    /**@SelfDocumented */
    val total get() = cash + card + underSalary + qrCode + internet

    /** Способ оплаты */
    val paymentMethod: PaymentMethod
        get() =
            when {
                internet > ZERO_MONEY_VALUE -> PaymentMethod.INTERNET
                qrCode > ZERO_MONEY_VALUE -> PaymentMethod.QR_CODE
                cash > ZERO_MONEY_VALUE && card.isZero() && underSalary.isZero() -> PaymentMethod.CASH
                card > ZERO_MONEY_VALUE && cash.isZero() && underSalary.isZero() -> PaymentMethod.CARD
                underSalary > ZERO_MONEY_VALUE && cash.isZero() && card.isZero() -> PaymentMethod.UNDER_SALARY
                else -> PaymentMethod.MIXED
            }

    /**@SelfDocumented */
    fun setToZero() {
        cash = ZERO_MONEY_VALUE
        card = ZERO_MONEY_VALUE
        underSalary = ZERO_MONEY_VALUE
        qrCode = ZERO_MONEY_VALUE
        internet = ZERO_MONEY_VALUE
    }

    /** Есть ли в составе суммы наличные деньги */
    val hasCashMoney get() = cash.isMoreZero()

    /** Есть ли в составе суммы деньги с банковской карты */
    val hasCardMoney get() = card.isMoreZero()

    /**@SelfDocumented */
    val isZero get() = total.isZero()
}