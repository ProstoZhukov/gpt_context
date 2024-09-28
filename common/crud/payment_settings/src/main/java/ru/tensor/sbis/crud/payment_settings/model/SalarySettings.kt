package ru.tensor.sbis.crud.payment_settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.retail_settings.generated.SalarySettings as ControllerSalarySettings

/** Настройки "Под зарплату". */
@Parcelize
data class SalarySettings(
    val allowSalary: Boolean,
    val isPersonalSettings: Boolean,
    val salaryIdentification: String,
    val salaryPayment: String
) : Parcelable {
    companion object {
        fun stub() = SalarySettings(
            false,
            false,
            "",
            ""
        )
    }
}

/** @SelfDocumented */
fun SalarySettings.map(): ControllerSalarySettings = ControllerSalarySettings(
    allowSalary = allowSalary,
    isPersonalSettings = isPersonalSettings,
    salaryIdentification = salaryIdentification,
    salaryPayment = salaryPayment
)

/** @SelfDocumented */
fun ControllerSalarySettings.map(): SalarySettings = SalarySettings(
    allowSalary = allowSalary,
    isPersonalSettings = isPersonalSettings,
    salaryIdentification = salaryIdentification,
    salaryPayment = salaryPayment
)
