package ru.tensor.sbis.crud.payment_settings.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.egais_cheque.generated.PingResult

/** Результат пинга УТМ. */
@Parcelize
data class PingUtmResult(
    val result: Boolean,
    val fsrar: String?,
    val errorMessage: String?
) : Parcelable

/** @SelfDocumented */
fun PingResult.toAndroidType(): PingUtmResult = PingUtmResult(
    result, fsrar, errorMessage
)

