package ru.tensor.sbis.crud.devices.settings.model

import ru.tensor.devices.settings.generated.AlcoType as ControllerAlcoType

/** Варианты разрешённого к продаже алкоголя. */
enum class AlcoType {

    /** @SelfDocumented */
    ALL,

    /** @SelfDocumented */
    MARKED,

    /** @SelfDocumented */
    UNMARKED;
}

/** @SelfDocumented */
fun ControllerAlcoType.map() = when (this) {
    ControllerAlcoType.ALL -> AlcoType.ALL
    ControllerAlcoType.MARKED -> AlcoType.MARKED
    ControllerAlcoType.UNMARKED -> AlcoType.UNMARKED
}

/** @SelfDocumented */
fun AlcoType.map() = when (this) {
    AlcoType.ALL -> ControllerAlcoType.ALL
    AlcoType.MARKED -> ControllerAlcoType.MARKED
    AlcoType.UNMARKED -> ControllerAlcoType.UNMARKED
}