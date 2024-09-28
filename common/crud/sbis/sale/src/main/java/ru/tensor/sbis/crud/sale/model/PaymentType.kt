package ru.tensor.sbis.crud.sale.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.sale.mobile.generated.OperationKkmPaymentType

/**
 * Тип оплаты
 */
@Parcelize
enum class PaymentType : Parcelable {
    /**@SelfDocumented */
    UNKNOWN,

    /**@SelfDocumented */
    CASH,

    /**@SelfDocumented */
    CARD,

    /**@SelfDocumented */
    QR_CODE,

    /**@SelfDocumented */
    INTERNET,

    /**@SelfDocumented */
    MIXED,

    /**@SelfDocumented */
    SALARY,

    /**@SelfDocumented */
    CERTIFICATE,

    /**@SelfDocumented */
    PREPAY,

    /**@SelfDocumented */
    POSTPAY,

    /**@SelfDocumented */
    NON_FISCAL,
    
    /** Оплата встречным представлением. */
    PROVISION,

    /** СПБ через терминал. */
    QR_CODE_TERMINAL,

    /**@SelfDocumented */
    PREFERENTIAL_CERTIFICATE,
}

/** @SelfDocumented */
fun OperationKkmPaymentType.map(): PaymentType =
    when (this) {
        OperationKkmPaymentType.CASH -> PaymentType.CASH
        OperationKkmPaymentType.CASHLESS -> PaymentType.CARD
        OperationKkmPaymentType.QR_CODE -> PaymentType.QR_CODE
        OperationKkmPaymentType.INTERNET -> PaymentType.INTERNET
        OperationKkmPaymentType.MIXED -> PaymentType.MIXED
        OperationKkmPaymentType.SALARY -> PaymentType.SALARY
        OperationKkmPaymentType.CERTIFICATE -> PaymentType.CERTIFICATE
        OperationKkmPaymentType.PREPAY -> PaymentType.PREPAY
        OperationKkmPaymentType.POSTPAY -> PaymentType.POSTPAY
        OperationKkmPaymentType.NON_FISCAL -> PaymentType.NON_FISCAL
        OperationKkmPaymentType.PROVISION -> PaymentType.PROVISION
        OperationKkmPaymentType.QR_CODE_TERMINAL -> PaymentType.QR_CODE_TERMINAL
        OperationKkmPaymentType.PREFERENTIAL_CERTIFICATE -> PaymentType.PREFERENTIAL_CERTIFICATE
    }