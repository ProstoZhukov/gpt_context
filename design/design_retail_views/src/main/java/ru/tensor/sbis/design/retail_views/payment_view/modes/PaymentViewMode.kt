@file:JvmName("PaymentViewMode")
package ru.tensor.sbis.design.retail_views.payment_view.modes

/** Режимы работы окна оплаты. */
sealed interface PaymentViewMode {

    companion object {

        /**
         * Поиск [PaymentViewMode] по индексу [modeIndex].
         * Вернет 'null', если [PaymentViewMode] не будет найден.
         */
        fun byIndex(modeIndex: Int): PaymentViewMode? {
            return when (modeIndex) {
                Advance.modeValue -> Advance
                DebtCredit.modeValue -> DebtCredit
                DepositWithDraw.modeValue -> DepositWithDraw
                Payment.modeValue -> Payment
                Refund.modeValue -> Refund

                /* Тип не определен. */
                else -> null
            }
        }

        /** Поиск [PaymentViewMode] по индексу [modeIndex]. */
        fun byIndexOrThrow(modeIndex: Int): PaymentViewMode {
            return byIndex(modeIndex) ?: throw Exception("Not found PaymentMode by index '$modeIndex'.")
        }
    }

    /** Идентификатор режима работы. */
    val modeValue: Int

    /** Аванс. */
    object Advance : PaymentViewMode {
        override val modeValue: Int = 0
    }

    /** Оплата кредита (долги). */
    object DebtCredit : PaymentViewMode {
        override val modeValue: Int = 1
    }

    /** Внесение/Изъятие. */
    object DepositWithDraw : PaymentViewMode {
        override val modeValue: Int = 2
    }

    /** Оплата (предоплата/в кредит/нефискальная). */
    object Payment : PaymentViewMode {
        override val modeValue: Int = 3
    }

    /** Возврат. */
    object Refund : PaymentViewMode {
        override val modeValue: Int = 4
    }
}