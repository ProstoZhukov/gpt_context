package ru.tensor.sbis.crud.devices.settings.model

import ru.tensor.sbis.retail_presto.g.PrintDensity as ControllerPrintDensity

/** Плотность печати. */
enum class PrintDensity {

    /**@SelfDocumented*/
    DPI_180,

    /**@SelfDocumented*/
    DPI_203,
}

/**@SelfDocumented*/
internal fun ControllerPrintDensity.toAndroidType() = when(this) {
    ControllerPrintDensity.DPI_180 -> PrintDensity.DPI_180
    ControllerPrintDensity.DPI_203 -> PrintDensity.DPI_203
}

/**@SelfDocumented*/
internal fun PrintDensity.toControllerType() = when(this) {
    PrintDensity.DPI_180 -> ControllerPrintDensity.DPI_180
    PrintDensity.DPI_203 -> ControllerPrintDensity.DPI_203
}