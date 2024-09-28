package ru.tensor.sbis.crud.payment_settings.model

import ru.tensor.sbis.retail_settings.generated.PriceType as ControllerPriceType

/**
 * Перечисление типов цен: FIXED_PRICE, FREE_PRICE
 */
enum class PriceType {

    FIXED_PRICE,
    FREE_PRICE
}

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerPriceType.map(): PriceType =
        when (this) {
            ControllerPriceType.FIXED_PRICE -> PriceType.FIXED_PRICE
            ControllerPriceType.FREE_PRICE -> PriceType.FREE_PRICE
        }

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun PriceType.map(): ControllerPriceType =
        when (this) {
            PriceType.FIXED_PRICE -> ControllerPriceType.FIXED_PRICE
            PriceType.FREE_PRICE  -> ControllerPriceType.FREE_PRICE
        }