package ru.tensor.sbis.crud.sale.model

import ru.tensor.sbis.sale.mobile.generated.RefusalReasonType as ControllerRefusalReasonType

/**
 * Перечисление типов причин возвратов/удалений: CANCEL, RETURN
 */
enum class RefusalReasonType {

    /**@SelfDocumented */
    CANCEL,

    /**@SelfDocumented */
    RETURN
}

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun RefusalReasonType.map(): ControllerRefusalReasonType =
        when (this) {
            RefusalReasonType.CANCEL -> ControllerRefusalReasonType.CANCEL
            RefusalReasonType.RETURN -> ControllerRefusalReasonType.RETURN
        }

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerRefusalReasonType.map(): RefusalReasonType =
        when (this) {
            ControllerRefusalReasonType.CANCEL -> RefusalReasonType.CANCEL
            ControllerRefusalReasonType.RETURN -> RefusalReasonType.RETURN
        }