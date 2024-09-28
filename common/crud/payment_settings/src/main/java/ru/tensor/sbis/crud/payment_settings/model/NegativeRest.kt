package ru.tensor.sbis.crud.payment_settings.model

import ru.tensor.sbis.retail_settings.generated.NegativeRest as ControllerNegativeRest

/**
 * Возможные значения флага продажи в минус
 */
enum class NegativeRest {
    ALLOW, // разрешена
    DENY, // запрещена
    WARNING // с предупреждением
}

/** @SelfDocumented */
fun ControllerNegativeRest.map(): NegativeRest =
    when (this) {
        ControllerNegativeRest.ALLOW -> NegativeRest.ALLOW
        ControllerNegativeRest.DENY -> NegativeRest.DENY
        ControllerNegativeRest.WARNING -> NegativeRest.WARNING
    }

/** @SelfDocumented */
fun NegativeRest.map(): ControllerNegativeRest =
    when (this) {
        NegativeRest.ALLOW -> ControllerNegativeRest.ALLOW
        NegativeRest.DENY -> ControllerNegativeRest.DENY
        NegativeRest.WARNING -> ControllerNegativeRest.WARNING
    }