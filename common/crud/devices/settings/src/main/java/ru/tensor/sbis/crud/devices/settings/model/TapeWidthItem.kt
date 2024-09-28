package ru.tensor.sbis.crud.devices.settings.model

import ru.tensor.sbis.retail_presto.g.TapeWidthItem as ControllerTapeWidthItem

/** Вариант пользовательского выбора для опции "Ширина ленты". */
data class TapeWidthItem(val tapeWidth: TapeWidth, val isActive: Boolean)

/**@SelfDocumented*/
fun ControllerTapeWidthItem.toAndroidType() = TapeWidthItem(
    tapeWidth = value.toAndroidType(),
    isActive = isActive
)

/**@SelfDocumented*/
fun TapeWidthItem.toControllerType() = ControllerTapeWidthItem(
    value = tapeWidth.toControllerType(),
    isActive = isActive
)