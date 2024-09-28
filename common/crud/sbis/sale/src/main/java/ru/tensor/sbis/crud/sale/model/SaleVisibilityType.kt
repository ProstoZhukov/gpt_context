package ru.tensor.sbis.crud.sale.model

import ru.tensor.sbis.sale.mobile.generated.VisibilityType as ControllerVisibilityType

/**
 * Перечисление типов видимости
 */
enum class SaleVisibilityType {
    /**@SelfDocumented */
    SHOW_ALL,

    /**@SelfDocumented */
    VISIBLE_ONLY,

    /**@SelfDocumented */
    INVISIBLE_ONLY;
}

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun SaleVisibilityType.map(): ControllerVisibilityType =
        when (this) {
            SaleVisibilityType.VISIBLE_ONLY -> ControllerVisibilityType.VISIBLE_ONLY
            SaleVisibilityType.INVISIBLE_ONLY -> ControllerVisibilityType.INVISIBLE_ONLY
            SaleVisibilityType.SHOW_ALL -> ControllerVisibilityType.SHOW_ALL
        }

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerVisibilityType.map(): SaleVisibilityType =
        when (this) {
            ControllerVisibilityType.VISIBLE_ONLY -> SaleVisibilityType.VISIBLE_ONLY
            ControllerVisibilityType.INVISIBLE_ONLY -> SaleVisibilityType.INVISIBLE_ONLY
            ControllerVisibilityType.SHOW_ALL -> SaleVisibilityType.SHOW_ALL
        }
