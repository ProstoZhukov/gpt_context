package ru.tensor.sbis.crud.devices.settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.devices.settings.generated.TaxSystem as ControllerTaxSystem

/**
 * Модель с описанием системы налогообложения
 */
@Parcelize
data class TaxSystem(val name: String, val code: Int) : Parcelable {
    companion object {
        fun stub(): TaxSystem = TaxSystem(
            "",
            0
        )
    }
}

/**
 * Маппер для преобразования модели контроллера во вью модель
 */
fun ControllerTaxSystem.map(): TaxSystem = TaxSystem(
    name,
    code
)

/**
 * Маппер для преобразования вью модели в модель контроллера
 */
fun TaxSystem.map(): ControllerTaxSystem = ControllerTaxSystem(
    name,
    code
)