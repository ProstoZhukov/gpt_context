package ru.tensor.sbis.design.decorators.number

import java.math.RoundingMode

/**
 * Режим округления, если количество знаков превышает порог [NumberDecoratorConfig.precision].
 *
 * @author ps.smirnyh
 */
enum class RoundMode(val mode: RoundingMode) {
    /** Без округления, просто отбрасывание лишних цифр. */
    TRUNC(RoundingMode.DOWN),

    /** Округление вверх при >= 0.5 и вниз при < 0.5. */
    ROUND(RoundingMode.HALF_UP)
}