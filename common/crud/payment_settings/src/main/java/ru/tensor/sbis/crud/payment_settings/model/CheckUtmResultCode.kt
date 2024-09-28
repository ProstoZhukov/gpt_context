package ru.tensor.sbis.crud.payment_settings.model


/**
 * Варианты возвращаемых значений code, при проверке УТМ.
 */
enum class CheckUtmResultCode(val code: Int) {
    SUCCESS(0),
    INCORRECT_FSRAR(1),
    UNAVAILABLE(2),
    INCORRECT_URL(3);

    companion object {
        fun from(code: Int) = values().find { it.code == code }
    }
}