package ru.tensor.sbis.crud.payment_settings.model

import ru.tensor.devices.settings.generated.MarkingScanMode as ControllerMarkingScanCode

/**@SelfDocumented */
enum class MarkingScanMode {

    /**@SelfDocumented */
    MANDATORY,

    /**@SelfDocumented */
    SKIPPABLE,

    /**@SelfDocumented */
    DESIRABLE
}

/**@SelfDocumented */
fun ControllerMarkingScanCode.toAndroid() = when(this) {
    ControllerMarkingScanCode.SKIPPABLE -> MarkingScanMode.SKIPPABLE
    ControllerMarkingScanCode.MANDATORY -> MarkingScanMode.MANDATORY
    ControllerMarkingScanCode.DESIRABLE -> MarkingScanMode.DESIRABLE
}

/**@SelfDocumented */
fun MarkingScanMode.toController() = when(this) {
    MarkingScanMode.SKIPPABLE -> ControllerMarkingScanCode.SKIPPABLE
    MarkingScanMode.MANDATORY -> ControllerMarkingScanCode.MANDATORY
    MarkingScanMode.DESIRABLE -> ControllerMarkingScanCode.DESIRABLE
}