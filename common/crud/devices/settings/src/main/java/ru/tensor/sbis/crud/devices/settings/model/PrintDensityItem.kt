package ru.tensor.sbis.crud.devices.settings.model

import ru.tensor.sbis.retail_presto.g.PrintDensityItem as ControllerPrintDensityItem

/** Вариант пользовательского выбора для опции "Плотность печати". */
data class PrintDensityItem(val printDensity: PrintDensity, val isActive: Boolean)

/**@SelfDocumented*/
fun ControllerPrintDensityItem.toAndroidType() = PrintDensityItem(
    printDensity = value.toAndroidType(),
    isActive = isActive
)

/**@SelfDocumented*/
fun PrintDensityItem.toControllerType() = ControllerPrintDensityItem(
    value = printDensity.toControllerType(),
    isActive = isActive
)