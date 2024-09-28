package ru.tensor.sbis.deals.feature

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Аргументы для мастера создания сделки.
 * @property preset предустановки, см. [Preset].
 *
 * @author aa.sviridov
 */
@Parcelize
data class DealsCreateArgs(
    val preset: Preset?,
) : Parcelable {

    /**
     * Предустановки для создаваемой сделки.
     * @property clientUuid идентификатор клиента для пропуска шага выбора клиентов.
     *
     * @author aa.sviridov
     */
    @Parcelize
    data class Preset(
        val clientUuid: UUID,
    ) : Parcelable
}