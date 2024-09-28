package ru.tensor.sbis.communication_decl.complain.data

/**
 * Результат жалобы.
 *
 * @author da.zhukov
 */
data class ComplainResult(
    /** Статус жалобы */
    val status: ComplainStatus,
    /** Сообщение жалобы */
    val message: String
)

/**
 * Статус жалобы.
 *
 * @author da.zhukov
 */
enum class ComplainStatus {
    /** Все хорошо */
    SUCCESS,
    /** Ошибка сети */
    NETWORK_ERROR,
    /** Другая ошибка */
    OTHER_ERROR
}