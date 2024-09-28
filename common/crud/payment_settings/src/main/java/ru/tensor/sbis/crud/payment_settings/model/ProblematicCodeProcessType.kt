package ru.tensor.sbis.crud.payment_settings.model

import ru.tensor.sbis.retail_settings.generated.ProblematicCode as ControllerProblematicCode

/** Настройка запретительного режима по части проблемных кодов маркировки. */
enum class ProblematicCodeProcessType {

    /** Запрет работы с проблемными кодами. */
    DENY,

    /** Разрешение работы с проблемными кодами, с предварительными предупреждениями. */
    WARN
}

/** @SelfDocumented */
fun ProblematicCodeProcessType.map(): ControllerProblematicCode {
    return when (this) {
        ProblematicCodeProcessType.DENY -> ControllerProblematicCode.DENY
        ProblematicCodeProcessType.WARN -> ControllerProblematicCode.WARN
    }
}

/** @SelfDocumented */
fun ControllerProblematicCode.map(): ProblematicCodeProcessType {
    return when (this) {
        ControllerProblematicCode.DENY -> ProblematicCodeProcessType.DENY
        ControllerProblematicCode.WARN -> ProblematicCodeProcessType.WARN
    }
}