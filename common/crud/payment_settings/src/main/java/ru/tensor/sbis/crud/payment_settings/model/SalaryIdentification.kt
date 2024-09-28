package ru.tensor.sbis.crud.payment_settings.model

import ru.tensor.sbis.retail_settings.generated.SalaryIdentification as ControllerSalaryIdentification

/**
 * Перечисление типов оплат под загрузку: EMPLOYEE_CARD
 */
enum class SalaryIdentification {
    EMPLOYEE_CARD,
    SELECTION_LIST
}

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun SalaryIdentification.map(): ControllerSalaryIdentification =
        when (this) {
            SalaryIdentification.EMPLOYEE_CARD  -> ControllerSalaryIdentification.EMPLOYEE_CARD
            SalaryIdentification.SELECTION_LIST -> ControllerSalaryIdentification.SELECTION_LIST
        }

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerSalaryIdentification.map(): SalaryIdentification =
        when (this) {
            ControllerSalaryIdentification.EMPLOYEE_CARD -> SalaryIdentification.EMPLOYEE_CARD
            ControllerSalaryIdentification.SELECTION_LIST -> SalaryIdentification.SELECTION_LIST
        }