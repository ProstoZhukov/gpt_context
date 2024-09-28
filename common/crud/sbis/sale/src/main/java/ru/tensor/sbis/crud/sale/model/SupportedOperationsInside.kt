package ru.tensor.sbis.crud.sale.model

import ru.tensor.devices.kkmservice.generated.SupportedOperations

/**
 * Объект с перечнем поддерживаемых операций KKM
 *
 * @property sale есть возможность продажи товара
 * @property saleReturn есть возможность возврата товара
 * @property cashIncome есть возможность внесения денег
 * @property cashOutcome есть возможность изъятия денег
 * @property receiptCorrection есть возможность печати чека коррекции
 * @property xReport есть возможность печати икс отчета
 * @property dateTimeSet есть возможность установки времени
 */
data class SupportedOperationsInside(
    val sale: Boolean,
    val saleReturn: Boolean,
    val cashIncome: Boolean,
    val cashOutcome: Boolean,
    val receiptCorrection: Boolean,
    val xReport: Boolean,
    val dateTimeSet: Boolean
)

/**@SelfDocumented */
fun SupportedOperations.map(): SupportedOperationsInside {
    return SupportedOperationsInside(
        this.sale,
        this.saleReturn,
        this.cashIncome,
        this.cashOutcome,
        this.receiptCorrection,
        this.xReport,
        this.dateTimeSet
    )
}