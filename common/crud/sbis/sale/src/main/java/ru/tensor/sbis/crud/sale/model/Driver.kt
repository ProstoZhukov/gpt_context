package ru.tensor.sbis.crud.sale.model

/**
 * Перечисление типов драйверов устройств
 */
enum class Driver(val title: String) {

    /**@SelfDocumented */
    ATOL("ATOL"),

    /**@SelfDocumented */
    VIRTUAL("Virtual"),

    /**@SelfDocumented */
    PURE_VIRTUAL("PureVirtual"),

    /**@SelfDocumented */
    SHTRIH("Shtrih"),

    /**@SelfDocumented */
    SHTRIKH("Shtrikh"),

    /**@SelfDocumented */
    EVOTOR("Evotor"),

    /**@SelfDocumented */
    MSPOS("Mspos"),

    /**@SelfDocumented */
    EMPTY(""),

    /**@SelfDocumented */
    SCANNER_KEYBOARD("ScannerKeyboard"),

    /**@SelfDocumented */
    @Suppress("unused")
    UMKA("Umka");
}

/** Виртуальная ли касса? */
fun isCashboxVirtual(driver: String?): Boolean {
    val driverType = driverFromString(driver)
    return driverType == Driver.VIRTUAL || driverType == Driver.PURE_VIRTUAL
}

/** @SelfDocumented */
fun driverFromString(driver: String?): Driver? {
    return Driver.values()
        .filter { it != Driver.EMPTY && it != Driver.SCANNER_KEYBOARD }
        .firstOrNull { it.title == driver }
}