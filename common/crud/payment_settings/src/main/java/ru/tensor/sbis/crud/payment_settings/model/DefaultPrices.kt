package ru.tensor.sbis.crud.payment_settings.model

import ru.tensor.sbis.retail_settings.generated.DefaultAlcoholPrice as ControllerDefaultAlcoholPrice
import ru.tensor.sbis.retail_settings.generated.DefaultTobaccoPrice as ControllerDefaultTobaccoPrice

/**
 * Цены по умолчанию на алкоголь
 */
enum class DefaultAlcoholPrice {
    MINIMUM,
    CURRENT
}

/** @SelfDocumented */
fun DefaultAlcoholPrice.map(): ControllerDefaultAlcoholPrice {
    return when (this) {
        DefaultAlcoholPrice.MINIMUM -> ControllerDefaultAlcoholPrice.MINIMUM
        DefaultAlcoholPrice.CURRENT -> ControllerDefaultAlcoholPrice.CURRENT
    }
}

/** @SelfDocumented */
fun ControllerDefaultAlcoholPrice.map(): DefaultAlcoholPrice {
    return when (this) {
        ControllerDefaultAlcoholPrice.MINIMUM -> DefaultAlcoholPrice.MINIMUM
        ControllerDefaultAlcoholPrice.CURRENT -> DefaultAlcoholPrice.CURRENT
    }
}

/**
 * Цены по умолчанию на табак
 */
enum class DefaultTobaccoPrice {
    MAXIMUM,
    CURRENT
}

/** @SelfDocumented */
fun DefaultTobaccoPrice.map(): ControllerDefaultTobaccoPrice {
    return when (this) {
        DefaultTobaccoPrice.MAXIMUM -> ControllerDefaultTobaccoPrice.MAXIMUM
        DefaultTobaccoPrice.CURRENT -> ControllerDefaultTobaccoPrice.CURRENT
    }
}

/** @SelfDocumented */
fun ControllerDefaultTobaccoPrice.map(): DefaultTobaccoPrice {
    return when (this) {
        ControllerDefaultTobaccoPrice.MAXIMUM -> DefaultTobaccoPrice.MAXIMUM
        ControllerDefaultTobaccoPrice.CURRENT -> DefaultTobaccoPrice.CURRENT
    }
}