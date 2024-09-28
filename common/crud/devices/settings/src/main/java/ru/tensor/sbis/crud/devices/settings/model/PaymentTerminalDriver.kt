package ru.tensor.sbis.crud.devices.settings.model

/**
 * Перечисление известных драйверов для терминалов и их строковых соответствий.
 * Со стороны оборудования присутствует гарантия о том, что соответствия не поменяются со временем.
 */
enum class PaymentTerminalDriver(val driverName: String) {
    EFTPOS("EFTPOS"),
    INPAS("INPAS"),
    SBERBANK("SberbankPilot"),
    EFTBASE("EFTBase");
}

/** Получить модель драйвера по его названию */
fun String.findPaymentTerminalDriverByName() = PaymentTerminalDriver.values().find { it.driverName == this }