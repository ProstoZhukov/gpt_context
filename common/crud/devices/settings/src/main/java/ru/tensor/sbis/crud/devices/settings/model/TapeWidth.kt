package ru.tensor.sbis.crud.devices.settings.model

import ru.tensor.sbis.retail_presto.g.TapeWidth as ControllerTapeWidth

/** Ширина ленты. */
enum class TapeWidth {

    /**@SelfDocumented*/
    MM_58,

    /**@SelfDocumented*/
    MM_72,

    /**@SelfDocumented*/
    MM_80,
}

/**@SelfDocumented*/
internal fun ControllerTapeWidth.toAndroidType() = when(this) {
    ControllerTapeWidth.MM_58 -> TapeWidth.MM_58
    ControllerTapeWidth.MM_72 -> TapeWidth.MM_72
    ControllerTapeWidth.MM_80 -> TapeWidth.MM_80
}

/**@SelfDocumented*/
internal fun TapeWidth.toControllerType() = when(this) {
    TapeWidth.MM_58 -> ControllerTapeWidth.MM_58
    TapeWidth.MM_72 -> ControllerTapeWidth.MM_72
    TapeWidth.MM_80 -> ControllerTapeWidth.MM_80
}