package ru.tensor.sbis.crud.payment_settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.egais_cheque.generated.CheckUtmResult as ControllerCheckUtmResult

/** Результат проверки УТМ. */
@Parcelize
data class CheckUtmResult(
    val code: CheckUtmResultCode?,
    val message: String
) : Parcelable

/** @SelfDocumented */
fun ControllerCheckUtmResult.toAndroidType(): CheckUtmResult = CheckUtmResult(
    CheckUtmResultCode.from(code), message
)

